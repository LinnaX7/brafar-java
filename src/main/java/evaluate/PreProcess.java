package evaluate;

import monitor.JDIDebuggerExecutor;
import program.ProgramBuilder;
import program.TesterBuilder;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import static evaluate.Evaluate.*;
import static sar.Fixer.isCompileError;


public class PreProcess {
    public static Path NEW_CORRECT_FOLDER = Paths.get(PROBLEM_FORMAT_FOLDER.toString(),"correct_new");
    public static Path NEW_WRONG_FOLDER = Paths.get(PROBLEM_FORMAT_FOLDER.toString(),"wrong_new");
    public static void splitP() {
        File[] files = new File(WRONG_FOLDER.toUri()).listFiles();
        File ss = new File(Paths.get(NEW_WRONG_FOLDER.toString()).toUri());
        int num = 0;
        if(ss.exists()){
            num = Objects.requireNonNull(ss.listFiles()).length;
        }else{
            ss.mkdirs();
        }
        System.out.println(num);
        for(int i = 0; i<files.length;i++) {
            if (files[i].isDirectory()) {
                String targetPath = Paths.get(files[i].getPath(),"src", JAVA_FILE_LIST[PROBLEM_NO-1]).toString();
                File targetF = new File(targetPath);
                if(targetF.exists()){
                    num ++;
                    File new_folder = new File(Paths.get(NEW_WRONG_FOLDER.toString(), String.format("%03d", num),"src").toString());
                    if(!new_folder.exists())
                        if(!new_folder.mkdirs())
                            System.out.println("failed to mkdir");
                    targetF.renameTo(new File(new_folder.getPath(), JAVA_FILE_LIST[PROBLEM_NO-1]));
                }
                else{
                    System.out.println(targetPath);
                }
            }
        }
    }

    public static void splitP1() {
        File[] files = new File(CORRECT_FOLDER.toUri()).listFiles();
        File ss = new File(Paths.get(NEW_CORRECT_FOLDER.toString()).toUri());
        int num = 0;
        if(ss.exists()){
            num = Objects.requireNonNull(ss.listFiles()).length;
        }else{
            ss.mkdirs();
        }
        System.out.println(num);
        for(int i = 0; i<files.length;i++) {
            if (files[i].isDirectory()) {
                String targetPath = Paths.get(files[i].getPath(),"src", JAVA_FILE_LIST[PROBLEM_NO-1]).toString();
                File targetF = new File(targetPath);
                if(targetF.exists()){
                    num ++;
                    File new_folder = new File(Paths.get(NEW_CORRECT_FOLDER.toString(), String.format("%03d", num),"src").toString());
                    if(!new_folder.exists())
                        if(!new_folder.mkdirs())
                            System.out.println("failed to mkdir");
                    targetF.renameTo(new File(new_folder.getPath(), JAVA_FILE_LIST[PROBLEM_NO-1]));
                }
                else{
                    System.out.println(targetPath);
                }
            }
        }
    }

    public static void evaluate() throws IOException {
        TesterBuilder testerBuilder = new TesterBuilder(TESTER_FOLDER.toString(), TESTER_CLASS_LIST[PROBLEM_NO-1]);
        File[] files = new File(WRONG_FOLDER.toUri()).listFiles();
        int num = 0;
        for(int i = 0; i<files.length;i++){
            if(files[i].isDirectory()){
                System.out.println(files[i].getPath());
                ProgramBuilder cp = new ProgramBuilder(Paths.get(files[i].getPath(),"src").toString(),  CLASS_File_LIST[PROBLEM_NO-1]);
                JDIDebuggerExecutor.preCompile();
                JDIDebuggerExecutor.compileTest(cp, testerBuilder);
                JDIDebuggerExecutor.executeAllTest(cp, testerBuilder, TARGET_METHOD_LIST[PROBLEM_NO-1]);
                if(isFailedTesterMethods()) {
                    System.out.println(files[i]);
                    File folder = new File(NEW_CORRECT_FOLDER.toString());
                    if(!folder.exists())
                        if(!folder.mkdirs())
                            System.out.println("failed to mkdir");
                    num +=1;
                    File new_folder = new File(Paths.get(NEW_CORRECT_FOLDER.toString(), String.format("%03d", num),"src").toString());
                    if(!new_folder.exists())
                        if(!new_folder.mkdirs())
                            System.out.println("failed to mkdir");
                    File target = new File(cp.getFilePath());
                    target.renameTo(new File(new_folder.getPath(), JAVA_FILE_LIST[PROBLEM_NO-1]));
                }
            }
        }
    }

    public static void evaluate1() throws IOException {
        TesterBuilder testerBuilder = new TesterBuilder(TESTER_FOLDER.toString(), TESTER_CLASS_LIST[PROBLEM_NO-1]);
        File[] files = new File(CORRECT_FOLDER.toUri()).listFiles();
        int num = 0;
        for(int i = 0; i<files.length;i++){
            if(files[i].isDirectory()){
                ProgramBuilder cp = new ProgramBuilder(Paths.get(files[i].getPath(),"src").toString(),  CLASS_File_LIST[PROBLEM_NO-1]);
                JDIDebuggerExecutor.preCompile();
                JDIDebuggerExecutor.compileTest(cp, testerBuilder);
                JDIDebuggerExecutor.executeAllTest(cp, testerBuilder, TARGET_METHOD_LIST[PROBLEM_NO-1]);
                if(!isFailedTesterMethods()) {
                    System.out.println(files[i]);
                    File folder = new File(NEW_WRONG_FOLDER.toString());
                    if(!folder.exists())
                        if(!folder.mkdirs())
                            System.out.println("failed to mkdir");
                    num +=1;
                    File new_folder = new File(Paths.get(NEW_WRONG_FOLDER.toString(), String.format("%03d", num),"src").toString());
                    if(!new_folder.exists())
                        if(!new_folder.mkdirs())
                            System.out.println("failed to mkdir");
                    File target = new File(cp.getFilePath());
                    target.renameTo(new File(new_folder.getPath(), JAVA_FILE_LIST[PROBLEM_NO-1]));
                }
            }
        }
    }

    public static void preCompile() throws Exception {
        TesterBuilder testerBuilder = new TesterBuilder(TESTER_FOLDER.toString(), TESTER_CLASS_LIST[PROBLEM_NO-1]);
        File[] files = new File(WRONG_FOLDER.toUri()).listFiles();
        int num = 0;
        for(int i = 0; i<files.length;i++){
            if(files[i].isDirectory()){
                System.out.println(files[i].getPath());
                ProgramBuilder cp = new ProgramBuilder(Paths.get(files[i].getPath(),"src").toString(),  CLASS_File_LIST[PROBLEM_NO-1]);
                JDIDebuggerExecutor.preCompile();
                JDIDebuggerExecutor.compileTest(cp, testerBuilder);
                if(isCompileError()){
                    System.out.println("Compile Error!");
                }
            }
        }
    }

    public static boolean isFailedTesterMethods() throws IOException {
        boolean flag = true;
        if(new File(LOG_PATH).exists()) {
            FileInputStream fStream = new FileInputStream(LOG_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
            String line = br.readLine();
            flag = true;
            while (line != null) {
                if(!line.contains("pass")) {
                    flag = false;
                    break;
                }
                line = br.readLine();
            }
            System.out.println(flag);
            fStream.close();
            br.close();
        }
        return flag;
    }

    public static void ss(){

        File[] files = new File(WRONG_FOLDER.toUri()).listFiles();

        for(int i = 47; i<files.length;i++){
            File folder = new File(Paths.get(WRONG_FOLDER.toString(), String.format("%03d", i),"src", JAVA_FILE_LIST[PROBLEM_NO-1]).toString());
            File new_folder = new File(Paths.get(WRONG_FOLDER.toString(), String.format("%03d", i-1),"src").toString());
            if(!new_folder.exists())
                if(!new_folder.mkdirs())
                    System.out.println("failed to mkdir");
            folder.renameTo(new File(new_folder.getPath(), JAVA_FILE_LIST[PROBLEM_NO-1]));
        }
    }

//    public static void main(String[] args) throws Exception {
////        ss();
//        preCompile();
//    }


}
