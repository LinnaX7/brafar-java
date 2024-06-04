package sar;

import cfs.Format;
import com.github.javaparser.ast.ImportDeclaration;
import monitor.JDIDebuggerExecutor;
import program.MethodBuilder;
import program.ProgramBuilder;
import program.TesterBuilder;
import program.block.BlockNode;
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

public class FixerWO {
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

    public boolean isCompileError() throws IOException {
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


    public String execute(ConfigBuilder configBuilder) throws Exception{
        Config config = configBuilder.getConfig();
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

        int patch = 1;
        boolean afl_stop=false;
        ArrayList<Integer>fixed=new ArrayList<>();
        System.out.println("--------------------------------------Init Code----------------------------------------");
        System.out.println(buggyProgram.getCompilationUnit());
        System.out.println();
        ArrayList<BlockNode> blockNodes = buggyProgram.getMethodByName(methodToFix).getMetaBlockNodes();
        this.fixResult=new TestResult(failedTesterMethods.size()==0);
        for(int i = 0; i<blockNodes.size(); i++){
            System.out.println("-----------------------------------------------------Repair " + patch +
                    "------------------------------------------------------");
            MethodBuilder buggyM = buggyProgram.getMethodByName(methodToFix);
            MethodBuilder correctM = correctProgram.getMethodByName(methodToFix);
            VariableMatch variableMatch = new VariableMatch(buggyM, correctM);
            Repair repair = new Repair(buggyM, correctM, variableMatch, null);
            repair.execRepair(blockNodes.get(i), fixed);
            String patchPath = Paths.get(Executor.ROOT_PATH, "tmp", String.format("patch%03d", patch),
                    "src").toString();
            buggyProgram.toPath(patchPath);
            patch += 1;
            buggyProgram = configBuilder.getProgram(config, patchPath);
            buggyProgram.initMethodBuilder(config.getFaultyMethodSignature());
            System.out.println("--------------------------------------Repair Result----------------------------------------");
            System.out.println(buggyProgram.getCompilationUnit());
            System.out.println();
        }
        JDIDebuggerExecutor.compileTest(buggyProgram, testerProgram);
        JDIDebuggerExecutor.executeAllTest(buggyProgram, testerProgram, methodToFix);
        initFailedTesterMethods();
        System.out.println("----------------------------------------------------------------------------------------");
        this.fixResult.setResult(failedTesterMethods.size()==0);
        System.out.println("Result:"+(this.fixResult.getResult()));
        return buggyProgram.getCompilationUnit().toString();
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

    public void initBlockValue(ProgramBuilder pb) throws IOException {
        pb.getMethodByName(methodToFix).setVariableValues(EXECUTE_LOG_PATH, pb.getClassName());
    }

    public void clearBlockValue(ProgramBuilder pb){
        pb.getMethodByName(methodToFix).clearBlocksBLP();
    }

    public TestResult getTestResult()throws IOException {
        return new TestResult(LOG_PATH);
    }
}
