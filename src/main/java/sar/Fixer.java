package sar;

import cfs.Format;
import com.github.javaparser.ast.ImportDeclaration;
import monitor.JDIDebuggerExecutor;
import program.MethodBuilder;
import program.ProgramBuilder;
import program.TesterBuilder;
import sar.config.Config;
import sar.config.ConfigBuilder;
import variables.VariableMatch;
import sar.repair.utils.Utils;
import sar.repair.Repair;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Fixer {
    private Config config;
    protected ProgramBuilder buggyProgram;
    protected ProgramBuilder correctProgram;
    protected TesterBuilder testerProgram;
    protected String methodToFix;
    protected List<String> failedTesterMethods = new ArrayList<>();
    public static final String LOG_PATH = Paths.get( Executor.ROOT_PATH,"tmp", "log.log").toString();
    public static final String EXECUTE_LOG_PATH = Paths.get(Executor.ROOT_PATH,"tmp", "execute.log").toString();
    public static final String COMPILE_LOG_PATH = Paths.get( Executor.ROOT_PATH,"tmp", "compile.log").toString();
    private TestResult fixResult;
    public static final int MAX_PATCHES = 35;

    public double execute_time = 0.0;
    public double repairTime = 0.0;

    public TestResult getFixResult() {
        return fixResult;
    }

    public double getExecute_time() {
        return execute_time;
    }

    public double getRepairTime() {
        return repairTime;
    }

    public void format(ProgramBuilder pb, ProgramBuilder tb) throws IOException {
        Format.formatCompositeNode(pb.getCompilationUnit());
        Format.formatCompositeNode(tb.getCompilationUnit());
        pb.toPath(Paths.get(Executor.ROOT_PATH, "tmp","format","wrong","src").toString());
        tb.toPath(Paths.get(Executor.ROOT_PATH,"tmp","format","correct","src").toString());
    }

    public void alignImport(){
        for(Map.Entry<String, ImportDeclaration> entry:correctProgram.getImportDeclarationMap().entrySet()){
            if(!buggyProgram.getImportDeclarationMap().containsKey(entry.getKey())){
                buggyProgram.getCompilationUnit().addImport(entry.getValue().clone());
            }
        }
    }

    public static boolean isCompileError() throws IOException {
        if(new File(COMPILE_LOG_PATH).exists()) {
            FileInputStream fStream = new FileInputStream(COMPILE_LOG_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
            String line = br.readLine();
            while(line!=null) {
                if(line.contains("错误")||line.contains("error"))
                    return true;
                line = br.readLine();
            }
            fStream.close();
            br.close();
        }
        return false;
    }

    public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                return;
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void preCompile(Config config) throws Exception {
        buggyProgram = config.getBuggyProgram();
        correctProgram = config.getReferenceProgram();
        testerProgram = config.getTesterProgram();
        methodToFix = config.getFaultyMethodSignature();
//        format(buggyProgram, correctProgram);
//        buggyProgram = new ProgramBuilder(Paths.get(Executor.ROOT_PATH, "tmp","format","wrong","src").toString(), buggyProgram.getClassName());
//        correctProgram = new ProgramBuilder(Paths.get(Executor.ROOT_PATH, "tmp","format","correct","src").toString(), correctProgram.getClassName());
        buggyProgram.initMethodBuilder(config.getFaultyMethodSignature());
        correctProgram.initMethodBuilder(config.getFaultyMethodSignature());
        alignImport();
        long startTime = System.currentTimeMillis();
        JDIDebuggerExecutor.preCompile();
        JDIDebuggerExecutor.compileTest(buggyProgram, testerProgram);
        JDIDebuggerExecutor.executeAllTest(buggyProgram, testerProgram, methodToFix);
        long endTime = System.currentTimeMillis();
        execute_time += (endTime - startTime) / 1000.0;
        if(isCompileError())
            System.out.println("Compile Error!");
    }


    public String execute(Config config) throws Exception{
        preCompile(config);
        initFailedTesterMethods();
        long startTime = 0;
        long endTime = 0;
        int patch = 1;
        boolean afl_stop=false;
        ArrayList<Integer>fixed=new ArrayList<>();
        System.out.println("--------------------------------------Init Code----------------------------------------");
        System.out.println(buggyProgram.getCompilationUnit());
        System.out.println();
        this.fixResult=new TestResult(failedTesterMethods.size()==0);
        while(failedTesterMethods.size()!=0) {
            if (patch > MAX_PATCHES || afl_stop) {
                break;
            }
            for (String failedTesterMethod : failedTesterMethods) {
                startTime = System.currentTimeMillis();
                JDIDebuggerExecutor.compileTest(correctProgram, testerProgram);
                JDIDebuggerExecutor.executeTest(correctProgram, testerProgram, methodToFix, failedTesterMethod);
                endTime = System.currentTimeMillis();
                execute_time += (endTime-startTime)/1000.0;

                clearBlockValue(correctProgram);
                initBlockValue(correctProgram);
                do {
                    startTime = System.currentTimeMillis();
                    JDIDebuggerExecutor.compileTest(buggyProgram, testerProgram);
                    endTime = System.currentTimeMillis();
                    execute_time += (endTime-startTime)/1000.0;
                    if(!isCompileError()) {
                        startTime = System.currentTimeMillis();
                        JDIDebuggerExecutor.executeTest(buggyProgram, testerProgram, methodToFix, failedTesterMethod);
                        endTime = System.currentTimeMillis();
                        execute_time += (endTime-startTime)/1000.0;
                    }else{
                        clearInfoForFile(LOG_PATH);
                        clearInfoForFile(EXECUTE_LOG_PATH);
                    }
                    clearBlockValue(buggyProgram);
                    initBlockValue(buggyProgram);
                    TestResult testResult= getTestResult();
                    if (testResult.getResult()) {
                        break;
                    }
                    if (patch > MAX_PATCHES||afl_stop) {
                        break;
                    }
                    //Repair
                    System.out.println("-----------------------------------------------------Repair " + patch +
                            "------------------------------------------------------");
                    MethodBuilder buggyM = buggyProgram.getMethodByName(methodToFix);
                    MethodBuilder correctM = correctProgram.getMethodByName(methodToFix);
                    if(testResult.isExcept()){
                        testResult.getException().setLocation(Integer.parseInt(buggyM.getBreakPointLines().get(buggyM.getBreakPointLines().size()-1)));
                    }
                    VariableMatch variableMatch = new VariableMatch(buggyM, correctM);
                    long ssTime = System.currentTimeMillis();
//                    AFL afl = new AFL(buggyM, correctM, variableMatch, fixed);
                    AFLW afl = new AFLW(buggyM, correctM, variableMatch, fixed);
                    Repair repair = new Repair(buggyM, correctM, variableMatch,testResult.getException());
                    if (afl.isNeedStop()) {
                        afl_stop = true;
                        Utils.changeFloatToDouble(repair.getCommon());
                        Utils.castReturnExp(repair.getCommon());
                    }else{
                        if (patch == MAX_PATCHES) {
                            repair.execRepair(afl.getFaultBlock(), fixed);
                            //Utils.changeFloatToDouble(repair.getCommon());
                        } else {
                            repair.execRepair(afl.getFaultBlock(), fixed);
                        }
                    }
                    long eeTime = System.currentTimeMillis();
                    repairTime += (eeTime-ssTime)/1000.0;
                    String patchPath = Paths.get(Executor.ROOT_PATH, "tmp", String.format("patch%03d", patch),
                            "src").toString();
                    buggyProgram.toPath(patchPath);
                    patch += 1;
                    buggyProgram = new ProgramBuilder(patchPath, config.getClassName());
                    buggyProgram.initMethodBuilder(config.getFaultyMethodSignature());
                    System.out.println("--------------------------------------Repair Result----------------------------------------");
                    System.out.println(buggyProgram.getCompilationUnit());
                    System.out.println();
                } while (true);
                if(getTestResult().getResult()){
                    continue;
                }
                if (patch > MAX_PATCHES || afl_stop) {
                    break;
                }
            }
            startTime = System.currentTimeMillis();
            JDIDebuggerExecutor.compileTest(buggyProgram, testerProgram);
            JDIDebuggerExecutor.executeAllTest(buggyProgram, testerProgram, methodToFix);
            endTime = System.currentTimeMillis();
            execute_time += (endTime-startTime)/1000.0;
            initFailedTesterMethods();
        }
        System.out.println("----------------------------------------------------------------------------------------");
        this.fixResult.setResult(failedTesterMethods.size()==0);
        System.out.println("Result:"+(this.fixResult.getResult()));
        buggyProgram.toPath(Paths.get("output","repaired","src").toString());
        return buggyProgram.getCompilationUnit().toString();
    }

    public void initBlockValue(ProgramBuilder pb) throws IOException {
        pb.getMethodByName(methodToFix).setVariableValues(EXECUTE_LOG_PATH, pb.getClassName());
    }

    public void clearBlockValue(ProgramBuilder pb){
        pb.getMethodByName(methodToFix).clearBlocksBLP();
    }

    public void initFailedTesterMethods() throws IOException {
        failedTesterMethods.clear();
        if(new File(LOG_PATH).exists()) {
            FileInputStream fStream = new FileInputStream(LOG_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
            String line = br.readLine();
            while (line != null) {
                if(line.contains("(")){
                    failedTesterMethods.add(line.substring(0, line.indexOf("(")));
                }
                line = br.readLine();
            }
            fStream.close();
            br.close();
        }
    }

    public TestResult getTestResult()throws IOException {
        return new TestResult(LOG_PATH);
    }
}
