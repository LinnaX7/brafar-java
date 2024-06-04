package monitor;

import program.EnvironmentConfig;
import program.ProgramBuilder;
import program.TesterBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class JDIDebuggerExecutor {
    public final static String ROOT_PATH = System.getProperty("user.dir");
    public final static String JUNIT_PATH = Paths.get(ROOT_PATH, "testlib", "junit-4.13.2.jar").toString();
    public final static String TOOL_PATH = Paths.get(ROOT_PATH, "testlib", "tools.jar").toString();
    public final static String SINGLE_JUNIT_PATH = Paths.get(ROOT_PATH, "src", "main", "java", "monitor", "SingleJUnitTestRunner.java").toString();
    public final static String CLASS_PATH = Paths.get(ROOT_PATH, "src", "main", "java").toString();
    public final static String JDI_DEBUGGER_PATH = Paths.get(ROOT_PATH, "src", "main", "java", "monitor", "JDIDebugger.java").toString();

    public static void compileTest(ProgramBuilder pb, TesterBuilder tb) throws IOException {
        String compileTesteeCMD = String.format("cd %s && javac -g -cp %s:. %s", Paths.get(pb.getParentPath()).toAbsolutePath(),
                TOOL_PATH, Paths.get(pb.getFilePath()).toAbsolutePath());

        String compileTesterCMD = String.format("cd %s && javac -g -cp %s:%s:%s:. %s", Paths.get(tb.getJavaParentPath()).toAbsolutePath(),
                TOOL_PATH, JUNIT_PATH, Paths.get(pb.getClassPath()).toAbsolutePath()+EnvironmentConfig.SLASH, Paths.get(tb.getFilePath()).toAbsolutePath());
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{EnvironmentConfig.SHELL, EnvironmentConfig.OPTION,
                    compileTesteeCMD + " && " + compileTesterCMD});
            while(true){
                if(!process.isAlive()){
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (process != null) {
                InputStreamReader read = new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8);
                File logDir = new File(Paths.get("tmp").toUri());
                if(!logDir.exists()) {
                    if(!logDir.mkdir()){
                        System.out.println("Failed to mkdir tmpDir!");
                    }
                }
                OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(Paths.get("tmp","compile.log").toString()));
                BufferedReader reader = new BufferedReader(read);
                BufferedWriter writer = new BufferedWriter(write);
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                process.destroy();
            }
        }
    }

    public static void preCompile(){
        String cdJDIDebuggerParent = String.format("cd %s", CLASS_PATH);
        String compileSingleJunitCMD = String.format("javac -g -cp %s:%s:. %s", TOOL_PATH, JUNIT_PATH, SINGLE_JUNIT_PATH);
        String compileJDIDebugger = String.format("javac -g -cp %s:. %s", TOOL_PATH, JDI_DEBUGGER_PATH);
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{EnvironmentConfig.SHELL, EnvironmentConfig.OPTION,
                    cdJDIDebuggerParent + " && " + compileSingleJunitCMD + " && " + compileJDIDebugger});
            while(true){
                if(!process.isAlive()){
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(process!=null)
                process.destroy();
        }
    }

    public static void executeCmd(String cdDir, String executeCMD) throws IOException {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{EnvironmentConfig.SHELL, EnvironmentConfig.OPTION,
                    cdDir + " && " + executeCMD});
//            ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", compileTesteeCMD);
//            Process p = pb.start();
//            int exitCode = p.waitFor();
//            long startTime = System.currentTimeMillis();
//            long currentTime = System.currentTimeMillis();
            while(true){
//                currentTime = System.currentTimeMillis();
                if(!process.isAlive()){
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//        }
        } finally {
            if (process != null) {
                InputStreamReader read = new InputStreamReader(process.getInputStream());
                OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream("log.log"));
                BufferedReader reader = new BufferedReader(read);
                BufferedWriter writer = new BufferedWriter(write);
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                process.destroy();
            }
        }
    }

    public static void executeAllTest(ProgramBuilder pb, TesterBuilder tb, String testeeMethod) throws IOException {
        String cdJDIDebuggerParent = String.format("cd %s", CLASS_PATH);
        String executeCMD = String.format("java -cp %s:%s:. %s %s %s %s %s %s %s", TOOL_PATH, Paths.get(pb.getClassPath()).toAbsolutePath()+EnvironmentConfig.SLASH, JDIDebugger.JDI_DEBUGGER,
                "Mode0", Paths.get(pb.getClassPath()).toAbsolutePath()+EnvironmentConfig.SLASH, pb.getClassName(), testeeMethod, Paths.get(tb.getClassPath()).toAbsolutePath()+EnvironmentConfig.SLASH, tb.getClassName());
        executeCmd(cdJDIDebuggerParent, executeCMD);
    }


    public static void executeTest(ProgramBuilder pb, TesterBuilder tb, String testeeMethod, String testerMethod) throws IOException {
        String cdJDIDebuggerParent = String.format("cd %s", CLASS_PATH);
        String executeCMD = String.format("java -cp %s:%s:. %s %s %s %s %s %s %s %s", TOOL_PATH, Paths.get(pb.getClassPath()).toAbsolutePath()+EnvironmentConfig.SLASH, JDIDebugger.JDI_DEBUGGER,
                "Mode1", Paths.get(pb.getClassPath()).toAbsolutePath()+EnvironmentConfig.SLASH, pb.getClassName(), testeeMethod, Paths.get(tb.getClassPath()).toAbsolutePath()+EnvironmentConfig.SLASH, tb.getClassName(), testerMethod);
        executeCmd(cdJDIDebuggerParent, executeCMD);
    }

    public static void main(String[] args) throws IOException {
        try {
            String rootPath = new File("").getAbsolutePath();
            String classPath = Paths.get(rootPath, "/data/Assignment05/Problem01/wrong/001/src").toString();
            ProgramBuilder testeeProgramBuilder = new ProgramBuilder(classPath, "hk.edu.polyu.comp.comp2021.assignment5.Unique");

            String testerClassPath = Paths.get(rootPath, "data", "Assignment5ForGrading", "test").toString();
            String testerClass = "hk.edu.polyu.comp.comp2021.assignment5.UniqueTest";
            TesterBuilder testerBuilder = new TesterBuilder(testerClassPath, testerClass);
            JDIDebuggerExecutor.compileTest(testeeProgramBuilder, testerBuilder);
            JDIDebuggerExecutor.executeTest(testeeProgramBuilder, testerBuilder, "unique_day", "test1");
            testeeProgramBuilder.initMethodBuilder("unique_day");
            testeeProgramBuilder.getMethodByName("unique_day").setVariableValues(Paths.get(rootPath, "tmp","execute.log").toString(), testeeProgramBuilder.getClassName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
