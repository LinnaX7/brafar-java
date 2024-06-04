package sar;

import ast.AstDiff;
import ast.JdtTreeContext;
import cfs.CFSVisitor;
import com.github.gumtreediff.tree.TreeContext;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import program.ProgramBuilder;
import sar.config.CmdOptions;
import sar.config.ConfigBuilder;
import variables.VariableMatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.getLevenshteinDistance;
import static sar.Main.parseCommandLine;

public class Executor {
    public final static String ROOT_PATH = System.getProperty("user.dir");
    public final static int ASSIGNMENT_NO = 1;
    public final static int PROBLEM_NO = 1;
    public final static String[][] JAVA_FILE_LIST = {{"MiniFloat.java", "SpecialNumber.java", "BalancedBrackets.java"},
            {"Rational.java", "Complex.java", "IntSet.java"},
            {"BaseNIntegerUnsigned.java", "Employee.java", "Manager.java"},
            {"BankAccount.java", "compset.java", "CompSet.java"},
            {"Unique.java"},
            {"Search.java"}};
    public final static String[][] TARGET_METHOD_LIST = {{"miniFloatFromString","isSpecial","isBalanced"},{"","",""},
            {"","",""},{"","",""},{"unique_day"},{"search"}};

    public static String PACKAGE_N = "";
    public static String[][] CLASS_File_LIST = {{"MiniFloat", "SpecialNumber", "BalancedBrackets"},
            {"Rational", "Complex", "IntSet"},
            {"BaseNIntegerUnsigned", "Employee", "Manager"},
            {"BankAccount", "compset", "CompSet"},
            {"Unique"},
            {"Search"}};
    public static String[][] TESTER_CLASS_LIST = {{"MiniFloatTest", "SpecialNumberTest", "BalancedBracketsTest"},
            {"Rational", "Complex", "IntSet"},
            {"BaseNIntegerUnsigned", "Employee", "Manager"},
            {"BankAccount", "compset", "CompSet"},
            {"UniqueTest"},{"SearchTest"}};
    public static Path PROBLEM_FOLDER = Paths.get(ROOT_PATH,"data", String.format("Assignment%02dFormat", ASSIGNMENT_NO),String.format("Problem%02d", PROBLEM_NO));
    public static Path CORRECT_FOLDER = Paths.get(PROBLEM_FOLDER.toString(),"correct");
    public static Path WRONG_FOLDER = Paths.get(PROBLEM_FOLDER.toString(),"wrong");
    public static Path TESTER_FOLDER = Paths.get(ROOT_PATH,"data", String.format("Assignment%dForGrading", ASSIGNMENT_NO),"test");
    public static String OUTPUT_PATH = Paths.get(ROOT_PATH, "output").toString();

    public static List<ProgramBuilder> getProgramBuilders(String folder) throws IOException {
        return getProgramBuilders(folder, ASSIGNMENT_NO, PROBLEM_NO);
    }

    public static List<ProgramBuilder> getProgramBuilders(String folderPath, int assignmentNo, int problemNo) throws IOException {
        List<ProgramBuilder> programBuilders = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.isDirectory() || Objects.requireNonNull(folder.listFiles()).length == 0) {
            System.out.println("Give a wrong folder to get treeContexts");
            return null;
        }
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.getPath().contains(".DS_Store") ||file.getPath().contains(".idea"))
                continue;
            String classPath = Paths.get(file.getPath(),"src").toString();
            String className = String.format(PACKAGE_N +"."+ CLASS_File_LIST[assignmentNo-1][problemNo-1]);
            ProgramBuilder programBuilder = new ProgramBuilder(classPath,className);
            CFSVisitor newCFS = new CFSVisitor(programBuilder.getTargetMethodDeclaration(TARGET_METHOD_LIST[assignmentNo-1][problemNo-1]));
            programBuilder.setMethodToFixCFS(newCFS);
            programBuilders.add(programBuilder);
        }
        return programBuilders;
    }

    public static JdtTreeContext getClosestJdtTreeByCFS(String methodCFS, List<JdtTreeContext> jdtTreeContexts) {
        int distance = Integer.MAX_VALUE;

        if (jdtTreeContexts.size() == 0) {
            return null;
        }
        JdtTreeContext tempJdtTree = jdtTreeContexts.get(0);
        for (JdtTreeContext jdtDst : jdtTreeContexts) {
            int dis = getLevenshteinDistance(methodCFS,jdtDst.getMethodCFS(TARGET_METHOD_LIST[ASSIGNMENT_NO -1][PROBLEM_NO -1]).getSimpleStruct());

            if (dis<distance) {
                distance = dis;
                tempJdtTree = jdtDst;
            }
        }
        return tempJdtTree;
    }



    public static String getFolderNum(String folder){
        return Paths.get(folder).getParent().getFileName().toString();
    }



    public static void write2Excel(String dsc, List<String> buggyFolders, List<String> buggyCodes,List<String> correctFolders, List<String> correctCodes, List<String> repairedCodes, boolean[]isFixed) throws IOException, WriteException {
        File file = new File(Paths.get(OUTPUT_PATH, String.format(dsc+"Assignment%02dProblem%02d.xls", ASSIGNMENT_NO, PROBLEM_NO)).toString());
        if(!file.exists()&&!file.createNewFile()){
            System.out.printf("Failed to create new file %s!", file.getPath());
            return;
        }
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet(dsc, 0);
        String[] titles;
        titles = new String[]{"Buggy_No", "Buggy_code", "Correct_No", "Correct_code", "Repaired_code", "is_fixed"};
        Label label;
        for (int i = 0; i < titles.length; i++) {
            label = new Label(i, 0, titles[i]);
            sheet.addCell(label);
        }
        for (int i = 0; i < buggyFolders.size(); i++) {
            label = new Label(0, i+1, buggyFolders.get(i));
            sheet.addCell(label);
            label = new Label(1, i+1, buggyCodes.get(i));
            sheet.addCell(label);
            label = new Label(2, i+1, correctFolders.get(i));
            sheet.addCell(label);
            label = new Label(3, i+1, correctCodes.get(i));
            sheet.addCell(label);
            label = new Label(4, i+1, repairedCodes.get(i));
            sheet.addCell(label);
            String temp = "true";
            if(!isFixed[i]){
                temp = "false";
            }
            label = new Label(5, i+1, temp);
            sheet.addCell(label);
        }
        workbook.write();
        workbook.close();
    }



    public static void main(String[] args) throws Exception {
//       Cluster.cluster();

//        outputSameCFSPairs();
        Workbook workbook = Workbook.getWorkbook(new File(Paths.get(OUTPUT_PATH, String.format("SameCFSAssignment%02dProblem%02d.xls", ASSIGNMENT_NO, PROBLEM_NO)).toString()));
        Sheet sheet = workbook.getSheet(0);
        String methodToFix = TARGET_METHOD_LIST[ASSIGNMENT_NO -1][PROBLEM_NO -1] + "@" + PACKAGE_N + "." + CLASS_File_LIST[ASSIGNMENT_NO -1][PROBLEM_NO -1];
        String testSourceDir =TESTER_FOLDER.toString();
        String testClass = PACKAGE_N + "." + TESTER_CLASS_LIST[ASSIGNMENT_NO -1][PROBLEM_NO -1];
        List<String> repairedCodes = new ArrayList<>();
        List<String> buggyFolders = new ArrayList<>();
        List<String> buggyCodes = new ArrayList<>();
        List<String> correctFolders = new ArrayList<>();
        List<String> correctCodes = new ArrayList<>();

        boolean[] isFixed = new boolean[sheet.getRows()];
        for (int i = 1; i < sheet.getRows(); i++) {
            System.out.println("----------------------------------------------------------File  "+ i+
                    "----------------------------------------------------------");
            if(new File("tmp").exists())
                FileUtils.cleanDirectory(new File("tmp"));
            Cell[] cells = sheet.getRow(i);
            buggyFolders.add(cells[0].getContents());
            buggyCodes.add(cells[1].getContents());
            correctFolders.add(cells[2].getContents());
            correctCodes.add(cells[3].getContents());
            String buggySourceDir = Paths.get(WRONG_FOLDER.toString(), cells[0].getContents(),"src").toString();
            String correctSourceDir = Paths.get(CORRECT_FOLDER.toString(), cells[2].getContents(),"src").toString();

            String []cmdArgs = String.format("--BuggyProgramSourceDir %s --ReferenceProgramSourceDir %s "+
                    "--MethodToFix %s --ProgramTestSourceDir %s --ProgramTestClass %s", buggySourceDir,correctSourceDir, methodToFix, testSourceDir, testClass).split(" ");
            CommandLine commandLine = parseCommandLine(CmdOptions.getCmdOptions(), cmdArgs);
            ConfigBuilder configBuilder = new ConfigBuilder();
            configBuilder.buildConfig(commandLine);
            Fixer fixer = new Fixer();
            String fixCode = fixer.execute(configBuilder.getConfig());
            VariableMatch.clearPreMatch();
            repairedCodes.add(fixCode);
            isFixed[i] = fixer.getTestResult().getResult();
            System.out.println("----------------------------------------------------------File  "+ i+
                    "----------------------------------------------------------");
            System.out.println();
        }
        write2Excel("RepairResult", buggyFolders, buggyCodes, correctFolders, correctCodes, repairedCodes, isFixed);
    }
}