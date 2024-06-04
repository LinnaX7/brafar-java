package evaluate;
import ast.AstDiff;
import ast.JdtTreeContext;
import cfs.CFSVisitor;
import cfs.Format;
import cfs.guider.CFBuilder;
import cfs.guider.CSNode;
import cfs.guider.EditScript;
import com.github.gumtreediff.tree.TreeContext;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import monitor.JDIDebuggerExecutor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.io.FileUtils;
import program.ProgramBuilder;
import program.TesterBuilder;
import sar.Fixer;
import sar.FixerWO;
import sar.TestResult;
import sar.config.CmdOptions;
import sar.config.ConfigBuilder;
import variables.VariableMatch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static ast.JdtTreeContext.getClosestJdtTree;
import static cfs.guider.CFGuider.*;
import static sar.Main.parseCommandLine;


public class Evaluate {
    public final static String ROOT_PATH = System.getProperty("user.dir");
    public static int PROBLEM_NO = 6;
    public final static String[] JAVA_FILE_LIST = {"MiniFloat.java", "SpecialNumber.java", "BalancedBrackets.java",
            "ScientificNotation.java", "LDLettersRemoval.java", "Base7.java"};

    public final static String[] TARGET_METHOD_LIST = {"miniFloatFromString","isSpecial","isBalanced",
            "getValueFromAeB", "removeLDLetters", "convertToBase7"};
    public static String PACKAGE_N = "";
    public static final String LOG_PATH = Paths.get( ROOT_PATH,"tmp", "log.log").toString();
    public static String[] CLASS_File_LIST = {"MiniFloat", "SpecialNumber", "BalancedBrackets",
            "ScientificNotation", "LDLettersRemoval", "Base7"};
    public static String[] TESTER_CLASS_LIST = {"MiniFloatTest", "SpecialNumberTest", "BalancedBracketsTest",
            "ScientificNotationTest", "LDLettersRemovalTest", "Base7Test"};
    public static Path PROBLEM_FOLDER = Paths.get(ROOT_PATH,"data", String.format("Problem%02d", PROBLEM_NO));
    public static Path PROBLEM_FORMAT_FOLDER = Paths.get(ROOT_PATH,"data", String.format("Problem%02dFormat", PROBLEM_NO));
    public static Path CORRECT_FOLDER = Paths.get(PROBLEM_FORMAT_FOLDER.toString(),"correct");
    public static Path WRONG_FOLDER = Paths.get(PROBLEM_FORMAT_FOLDER.toString(),"wrong");

    public static Path TESTER_FOLDER = Paths.get(PROBLEM_FOLDER.toString(),"test");
    public static String OUTPUT_PATH = Paths.get(ROOT_PATH, "output").toString();

    public static String OUTPUT_FOLDER = Paths.get(ROOT_PATH, "output",
            String.format("Problem%02d", PROBLEM_NO)).toString();

    public static Path FORMAT_FOLDER = Paths.get(ROOT_PATH, "data", String.format("Problem%02dFormat", PROBLEM_NO));

    public static Path CHATGPT_PROBLEM_FOLDER = Paths.get(ROOT_PATH,"chatgpt", String.format("Problem%02dFormat", PROBLEM_NO));
//    public static Path CHATGPT_PROBLEM_FOLDER = Paths.get(ROOT_PATH,"chatgpt", String.format("Problem%02d", PROBLEM_NO));
    public static Path CHATGPT_WRONG_FOLDER = Paths.get(CHATGPT_PROBLEM_FOLDER.toString(),"wrong");


    public static ArrayList<ProgramBuilder> getProgramsFromFolder(String folderPath, String className, String methodToFix) throws IOException {
        ArrayList<ProgramBuilder> programs = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.isDirectory() || Objects.requireNonNull(folder.listFiles()).length == 0) {
            System.out.printf("Give a wrong folder to get programs of %s!\n", folderPath);
            return programs;
        }
        File[] files = folder.listFiles();
        if(files == null)
            return programs;
        for (File file : files) {
            if (file.getPath().contains(".DS_Store") ||file.getPath().contains(".idea"))
                continue;
            System.out.println(file.getPath());
            String classPath = Paths.get(file.getPath(),"src").toString();
            ProgramBuilder program = new ProgramBuilder(classPath, className);
            programs.add(program);
        }
        return programs;
    }

    public static ArrayList<ProgramBuilder> getProgramsFromWrongFolder(String folderPath, String className) throws IOException {
        ArrayList<ProgramBuilder> programs = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.isDirectory() || Objects.requireNonNull(folder.listFiles()).length == 0) {
            System.out.printf("Give a wrong folder to get programs of %s!\n", folderPath);
            return programs;
        }
        File[] files = folder.listFiles();
        if(files == null)
            return programs;
        for (File file : files) {
            if (file.getPath().contains(".DS_Store") ||file.getPath().contains(".idea"))
                continue;
            System.out.println(file.getPath());
            String classPath = Paths.get(file.getPath(),"wrong","src").toString();
            ProgramBuilder program = new ProgramBuilder(classPath, className);
            programs.add(program);
        }
        return programs;
    }

    public static void formatFolders() throws IOException {
        ArrayList<ProgramBuilder> buggyPs = getProgramsFromFolder(Paths.get(PROBLEM_FOLDER.toString(),"wrong").toString(), CLASS_File_LIST[PROBLEM_NO-1], TARGET_METHOD_LIST[PROBLEM_NO-1]);
        ArrayList<ProgramBuilder> correctPs = getProgramsFromFolder(Paths.get(PROBLEM_FOLDER.toString(),"correct").toString(), CLASS_File_LIST[PROBLEM_NO-1], TARGET_METHOD_LIST[PROBLEM_NO-1]);
        for(ProgramBuilder buggyP:buggyPs){
            String wrongNum = Paths.get(buggyP.getClassPath()).getParent().getFileName().toString();
            MethodDeclaration targetMethodD = buggyP.getMethodDeclarationMap().get(TARGET_METHOD_LIST[PROBLEM_NO-1]);
            if(targetMethodD == null)
                System.out.printf("error parser file %s%n", wrongNum);
            else {
                Format.formatProgram(targetMethodD);
            }
            buggyP.toPath(Paths.get(FORMAT_FOLDER.toString(), "wrong", wrongNum, "src").toString());
        }
        for(ProgramBuilder correctP:correctPs){
            String correctNum = Paths.get(correctP.getClassPath()).getParent().getFileName().toString();
            MethodDeclaration targetMethodD = correctP.getMethodDeclarationMap().get(TARGET_METHOD_LIST[PROBLEM_NO-1]);
            if(targetMethodD == null)
                System.out.printf("error parser file %s%n", correctNum);
            else {
                Format.formatProgram(targetMethodD);
            }
            correctP.toPath(Paths.get(FORMAT_FOLDER.toString(), "correct", correctNum, "src").toString());
        }
    }

    public static void initMethod2FixForPrograms(ArrayList<ProgramBuilder> ps, String methodToFix){
        for(ProgramBuilder program:ps){
            MethodDeclaration targetMethodD = program.getMethodDeclarationMap().get(methodToFix);
            CFSVisitor newCFS = new CFSVisitor(targetMethodD);
            program.setMethodToFixCFS(newCFS);
            program.initMethodBuilder(TARGET_METHOD_LIST[PROBLEM_NO-1]);
        }
    }

    public static List<String> outputSameCFSPairs(ArrayList<ProgramBuilder> buggyPs, ArrayList<ProgramBuilder> correctPs) throws IOException, WriteException {
        List<ProgramBuilder> w2cPrograms = new ArrayList<>();
        List<ProgramBuilder> wProgram = new ArrayList<>();
        List<String> wrongFolders = new ArrayList<>();
        for(ProgramBuilder buggyP:buggyPs){
            ProgramBuilder correctP = getProgramBySameCFS(buggyP.getMethodToFixCFS().getStrCFS(), correctPs);
            if(correctP != null){
                wProgram.add(buggyP);
                w2cPrograms.add(correctP);
                String buggyNum = getFolderNum(buggyP.getClassPath());
                wrongFolders.add(buggyNum);
                buggyP.toPath(Paths.get(OUTPUT_FOLDER, buggyNum, "wrong", "src").toString(), buggyP.getCompilationUnit());
                correctP.toPath(Paths.get(OUTPUT_FOLDER, buggyNum, "correct", "src").toString(), correctP.getCompilationUnit());
            }
        }
        File outputDir = new File(OUTPUT_PATH);
        if(!outputDir.exists()&&!outputDir.mkdir()){
            System.out.printf("Failed to mkdir %s!", OUTPUT_PATH);
            return null;
        }

        write2Excel("SameCFS", wProgram, w2cPrograms);
        return wrongFolders;
    }

    public static String getFolderNum(String folder){
        return Paths.get(folder).getParent().getFileName().toString();
    }

    public static void write2Excel(String dsc, List<ProgramBuilder> buggyPs, List<ProgramBuilder> correctPs) throws IOException, WriteException {
        File file = new File(Paths.get(OUTPUT_PATH, String.format(dsc+"Problem%02d.xls",PROBLEM_NO)).toString());
        if(!file.exists()&&!file.createNewFile()){
            System.out.printf("Failed to create new file %s!", file.getPath());
            return;
        }
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet(dsc, 0);
        String[] titles;
        titles = new String[]{"Buggy_No", "Buggy_code", "Correct_No", "Correct_code"};
        Label label;
        for (int i = 0; i < titles.length; i++) {
            label = new Label(i, 0, titles[i]);
            sheet.addCell(label);
        }
        for (int i = 0; i < buggyPs.size(); i++) {
            label = new Label(0, i+1, getFolderNum(buggyPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(1, i+1, buggyPs.get(i).getCompilationUnit().toString());
            sheet.addCell(label);
            label = new Label(2, i+1, getFolderNum(correctPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(3, i+1, correctPs.get(i).getCompilationUnit().toString());
            sheet.addCell(label);
        }
        workbook.write();
        workbook.close();
    }

    public static void write2ExcelCFGuider(String dsc, List<ProgramBuilder> buggyPs, List<ProgramBuilder> correctPs, List<Integer> nodeNums, List<Integer> nodeNums2,
                                           List<Integer> dist, List<Double> time,List<Double> time2,List<Boolean> isBROSuccess) throws IOException, WriteException, BiffException {
        File file = new File(Paths.get(OUTPUT_PATH, String.format(dsc+"Problem%02d.xls",PROBLEM_NO)).toString());
        Workbook preWorkbook = null;
        WritableWorkbook workbook;
        if(!file.exists()){
            if(file.createNewFile()){

            }else{
                System.out.printf("Failed to create new file %s!", file.getPath());
                return;
            }
        }else{
            preWorkbook = Workbook.getWorkbook(file);
        }

        if(preWorkbook !=null){
            workbook = Workbook.createWorkbook(file, preWorkbook);
        }else{
            workbook = Workbook.createWorkbook(file);
        }
        WritableSheet sheet;
        Label label;
        if(preWorkbook!=null) {
            sheet = workbook.getSheet(0);
        }else {
            sheet = workbook.createSheet(dsc, 0);
            String[] titles;
            titles = new String[]{"Buggy_No", "Buggy_code", "Correct_No", "Correct_code", "Buggy_CFNodes", "Correct_CFNodes", "Edit_Dist", "BR_Time", "BRWO_Time", "isBRWOSuccess"};
            for (int i = 0; i < titles.length; i++) {
                label = new Label(i, 0, titles[i]);
                sheet.addCell(label);
            }
        }
        int temp = sheet.getRows();
        for (int i = 0; i < buggyPs.size(); i++) {
            label = new Label(0, i+temp, getFolderNum(buggyPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(1, i+temp, buggyPs.get(i).getCompilationUnit().toString());
            sheet.addCell(label);
            label = new Label(2, i+temp, getFolderNum(correctPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(3, i+temp, correctPs.get(i).getCompilationUnit().toString());
            sheet.addCell(label);
            label = new Label(4, i+temp, nodeNums.get(i).toString());
            sheet.addCell(label);
            label = new Label(5, i+temp, nodeNums2.get(i).toString());
            sheet.addCell(label);
            label = new Label(6, i+temp, dist.get(i).toString());
            sheet.addCell(label);
            label = new Label(7, i+temp, time.get(i).toString());
            sheet.addCell(label);
            label = new Label(8, i+temp, time2.get(i).toString());
            sheet.addCell(label);
            label = new Label(9, i+temp, isBROSuccess.get(i).toString());
            sheet.addCell(label);
        }
        workbook.write();
        workbook.close();
    }

    public static void write2ExcelA(String dsc, List<JdtTreeContext> buggyPs, List<JdtTreeContext> correctPs, ArrayList<Double> time)  throws IOException, WriteException {
        File file = new File(Paths.get(OUTPUT_PATH, String.format(dsc+"Problem%02d.xls",PROBLEM_NO)).toString());
        if(!file.exists()&&!file.createNewFile()){
            System.out.printf("Failed to create new file %s!", file.getPath());
            return;
        }
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet(dsc, 0);
        String[] titles;
        titles = new String[]{"Buggy_No", "Buggy_code", "Correct_No", "Correct_code","Search_time"};
        Label label;
        for (int i = 0; i < titles.length; i++) {
            label = new Label(i, 0, titles[i]);
            sheet.addCell(label);
        }
        for (int i = 0; i < buggyPs.size(); i++) {
            label = new Label(0, i+1, getFolderNum(buggyPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(1, i+1, buggyPs.get(i).getFileContext());
            sheet.addCell(label);
            label = new Label(2, i+1, getFolderNum(correctPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(3, i+1, correctPs.get(i).getFileContext());
            sheet.addCell(label);
            label = new Label(4, i+1, time.get(i).toString());
            sheet.addCell(label);
        }
        workbook.write();
        workbook.close();
    }

    public static void write2ExcelB(String dsc, List<ProgramBuilder> buggyPs, List<ProgramBuilder> correctPs, ArrayList<Double> time)  throws IOException, WriteException {
        File file = new File(Paths.get(OUTPUT_PATH, String.format(dsc+"Problem%02d.xls",PROBLEM_NO)).toString());
        if(!file.exists()&&!file.createNewFile()){
            System.out.printf("Failed to create new file %s!", file.getPath());
            return;
        }
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet(dsc, 0);
        String[] titles;
        titles = new String[]{"Buggy_No", "Buggy_code", "Correct_No", "Correct_code","Search_time"};
        Label label;
        for (int i = 0; i < titles.length; i++) {
            label = new Label(i, 0, titles[i]);
            sheet.addCell(label);
        }
        for (int i = 0; i < buggyPs.size(); i++) {
            label = new Label(0, i+1, getFolderNum(buggyPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(1, i+1, buggyPs.get(i).getCompilationUnit().toString());
            sheet.addCell(label);
            label = new Label(2, i+1, getFolderNum(correctPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(3, i+1, correctPs.get(i).getCompilationUnit().toString());
            sheet.addCell(label);
            label = new Label(4, i+1, time.get(i).toString());
            sheet.addCell(label);
        }
        workbook.write();
        workbook.close();
    }

    public static void write2ExcelC(String dsc, List<ProgramBuilder> buggyPs, ArrayList<Boolean> isFixed)  throws IOException, WriteException {
        File file = new File(Paths.get(OUTPUT_PATH, String.format(dsc+"Problem%02d.xls",PROBLEM_NO)).toString());
        if(!file.exists()&&!file.createNewFile()){
            System.out.printf("Failed to create new file %s!", file.getPath());
            return;
        }
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet(dsc, 0);
        String[] titles;
        titles = new String[]{"Buggy_No", "is_Fixed"};
        Label label;
        for (int i = 0; i < titles.length; i++) {
            label = new Label(i, 0, titles[i]);
            sheet.addCell(label);
        }
        for (int i = 0; i < buggyPs.size(); i++) {
            label = new Label(0, i+1, getFolderNum(Paths.get(buggyPs.get(i).getClassPath()).getParent().toString()));
            sheet.addCell(label);
            label = new Label(1, i+1, isFixed.get(i).toString());
            sheet.addCell(label);
        }
        workbook.write();
        workbook.close();
    }

    public static ProgramBuilder getProgramBySameCFS(String methodCFS, List<ProgramBuilder> programBuilders) {
        if (programBuilders.size() == 0) {
            return null;
        }
        if(methodCFS.equals("")){
            return null;
        }

        for (ProgramBuilder program : programBuilders) {
            if(methodCFS.equals(program.getMethodToFixCFS().getStrCFS())){
                return program;
            }
        }
        return null;
    }


    public static void evaluate(String inputPath, String outputPath, String methodName) throws BiffException, IOException, WriteException {
        Workbook workbook=Workbook.getWorkbook(new File(inputPath));
        WritableWorkbook workbookCopy=Workbook.createWorkbook(new File(outputPath),workbook);
        WritableSheet sheet = workbookCopy.getSheet(0);
        int rowNum=sheet.getRows();
        Label label;
        label = new Label(6, 0, "modify");
        sheet.addCell(label);

        label=new Label(7,0,"rows");
        sheet.addCell(label);

        label=new Label(8,0,"cfs");
        sheet.addCell(label);
        for(int i=1;i<rowNum;i++){
            Cell[] row=sheet.getRow(i);
            //modify rate calculator
            if(row[5].getContents().equals("true")){
                ASTEditDistanceCalculator astEditDistanceCalculator =
                        new ASTEditDistanceCalculator(row[1].getContents(), row[4].getContents(),
                                methodName,methodName);
                //Relative Patch Size (RPS) Tree-Edit-Distance (TED)
                //RPS = TED(AST b , AST r )/Size(AST b )
//                System.out.print(astEditDistanceCalculator.getDistance());
//                System.out.print("\t");
//                System.out.print(astEditDistanceCalculator.buggyASTSize);
                float RPS=((float)astEditDistanceCalculator.getDistance())/
                        ((float)astEditDistanceCalculator.buggyASTSize);
//                System.out.print("\t");
//                System.out.println(RPS);
                label=new Label(6,i,String.format("%.2f",RPS));
                sheet.addCell(label);
            }
            //code rows calculator
            CodeRowsCalculator codeRowsCalculator=new CodeRowsCalculator(row[1].getContents(),methodName);
            label=new Label(7,i,String.format("%d",codeRowsCalculator.getRows()));
            sheet.addCell(label);
            //cfs nodes calculator
            CFSCalculator cfsCalculator=new CFSCalculator(row[1].getContents(),methodName);
            label=new Label(8,i,String.format("%d",cfsCalculator.getCfs()));
            sheet.addCell(label);
        }
        workbookCopy.write();
        workbookCopy.close();
        workbook.close();
    }



    public static ArrayList<JdtTreeContext> getTreeContexts(String folderPath) throws IOException {
        ArrayList<JdtTreeContext> treeContexts = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.isDirectory() || Objects.requireNonNull(folder.listFiles()).length == 0) {
            System.out.printf("Give a wrong folder to get programs of %s!\n", folderPath);
            return treeContexts;
        }
        File[] files = folder.listFiles();
        if(files == null)
            return treeContexts;
        for (File file : files) {
            if (file.getPath().contains(".DS_Store") || file.getPath().contains(".idea"))
                continue;
            System.out.println(file.getPath());
            String classPath = Paths.get(file.getPath(), "src").toString();
            JdtTreeContext pTC = new JdtTreeContext(classPath, JAVA_FILE_LIST[PROBLEM_NO-1], classPath, CLASS_File_LIST[PROBLEM_NO-1]);
            treeContexts.add(pTC);
        }
        return treeContexts;
    }

    public static ArrayList<JdtTreeContext> getTreeContexts(ArrayList<String> fileList) throws IOException {
        ArrayList<JdtTreeContext> treeContexts = new ArrayList<>();
        for (String file:fileList) {
            JdtTreeContext pTC = new JdtTreeContext(file, JAVA_FILE_LIST[PROBLEM_NO-1], file, CLASS_File_LIST[PROBLEM_NO-1]);
            treeContexts.add(pTC);
        }
        return treeContexts;
    }


    public static void searchByAST() throws IOException, WriteException {
        ArrayList<JdtTreeContext> buggyTCs = getTreeContexts(WRONG_FOLDER.toString());
        ArrayList<JdtTreeContext> correctTCs = getTreeContexts(CORRECT_FOLDER.toString());
        ArrayList<JdtTreeContext> w2cTCs = new ArrayList<>();
        ArrayList<Double> time = new ArrayList<>();
        for(JdtTreeContext buggyTC:buggyTCs){

            long startTime = System.currentTimeMillis();
            w2cTCs.add(getClosestJdtTree(buggyTC.getTreeContext(), correctTCs, TARGET_METHOD_LIST[PROBLEM_NO-1]));
            long endTime = System.currentTimeMillis();
            time.add((endTime - startTime) / 1000.0);

//            System.out.println(endTime-startTime);
        }
        write2ExcelA("Closest(Ast)", buggyTCs, w2cTCs,time);
    }

    public static void searchByCFSandAST(ArrayList<ProgramBuilder> buggyPs, ArrayList<ProgramBuilder> correctPs, List<String> wrongFolders) throws IOException, WriteException {
        ArrayList<JdtTreeContext> buggyTCs = new ArrayList<>();
        ArrayList<JdtTreeContext> w2cTCs = new ArrayList<>();
        ArrayList<Double> time = new ArrayList<>();
        for(ProgramBuilder buggyP:buggyPs){
            if(!wrongFolders.contains(getFolderNum(buggyP.getClassPath()))) {
                long startTime = System.currentTimeMillis();
                String wrongNum = Paths.get(buggyP.getClassPath()).getParent().getFileName().toString();
                ArrayList<CFBuilder> kthClosestPrograms = findKthClosestPrograms(buggyP, correctPs,TARGET_METHOD_LIST[PROBLEM_NO-1]);
                JdtTreeContext buggyJdt = new JdtTreeContext(buggyP.getClassPath(), JAVA_FILE_LIST[PROBLEM_NO - 1], buggyP.getClassPath(), CLASS_File_LIST[PROBLEM_NO - 1]);
                ArrayList<JdtTreeContext> kthClosestJdTrees = new ArrayList<>();
                buggyTCs.add(buggyJdt);
                for (int i = 0; i < K; i++) {
                    kthClosestJdTrees.add(new JdtTreeContext(kthClosestPrograms.get(i).getProgramBuilder().getClassPath(), JAVA_FILE_LIST[PROBLEM_NO - 1], kthClosestPrograms.get(i).getProgramBuilder().getClassPath(), CLASS_File_LIST[PROBLEM_NO - 1]));
                }
                w2cTCs.add(getClosestJdtTree(buggyJdt.getTreeContext(), kthClosestJdTrees,TARGET_METHOD_LIST[PROBLEM_NO - 1]));
                long endTime = System.currentTimeMillis();
                time.add((endTime - startTime) / 1000.0);
            }
        }
        write2ExcelA("Closest(CFS+AST)", buggyTCs, w2cTCs,time);
    }

    public static void searchByCFS() throws IOException, WriteException {
        ArrayList<ProgramBuilder> buggyPs = getProgramsFromFolder(WRONG_FOLDER.toString(), CLASS_File_LIST[PROBLEM_NO-1],TARGET_METHOD_LIST[PROBLEM_NO-1]);
        ArrayList<ProgramBuilder> correctPs = getProgramsFromFolder(CORRECT_FOLDER.toString(), CLASS_File_LIST[PROBLEM_NO-1],TARGET_METHOD_LIST[PROBLEM_NO-1]);
        ArrayList<ProgramBuilder> w2cTCs = new ArrayList<>();
        ArrayList<Double> time = new ArrayList<>();
        for(ProgramBuilder buggyP:buggyPs){

            long startTime = System.currentTimeMillis();
            String wrongNum = Paths.get(buggyP.getClassPath()).getParent().getFileName().toString();
//            if(wrongNum.equals("009")) {
            ArrayList<CFBuilder> kthClosestPrograms = findKthClosestPrograms(buggyP, correctPs, TARGET_METHOD_LIST[PROBLEM_NO-1]);
            w2cTCs.add(controlFlowGuide(buggyP, kthClosestPrograms, TARGET_METHOD_LIST[PROBLEM_NO-1]));
//            }
            long endTime = System.currentTimeMillis();
            time.add((endTime - startTime) / 1000.0);

        }
        write2ExcelB("Closest(CFS)", buggyPs, w2cTCs,time);
    }


    public static void outPutCFGuider() throws IOException, WriteException, BiffException, ExecutionException, InterruptedException {
        File outPutFolder = new File(OUTPUT_FOLDER);
        if(!outPutFolder.exists()&&!outPutFolder.mkdirs()){
            System.out.printf("Failed to create folder %s", OUTPUT_FOLDER);
            return;
        }
        File searchResult = new File(Paths.get(OUTPUT_PATH,String.format("Closest(CFS+AST)Problem%02d.xls", PROBLEM_NO)).toString());
        Workbook workbook = Workbook.getWorkbook(searchResult);
        Sheet sheet = workbook.getSheet(0);
        ArrayList<ProgramBuilder> buggyPs = new ArrayList<>();
        ArrayList<ProgramBuilder> correctPs = new ArrayList<>();
        ArrayList<Integer> buggyCSNums = new ArrayList<>();
        ArrayList<Integer> correctNums = new ArrayList<>();
        ArrayList<Integer> editDist = new ArrayList<>();
        ArrayList<Double> BR_time = new ArrayList<>();
        ArrayList<Double> BRO_time = new ArrayList<>();
        ArrayList<Boolean> isBROSuccess = new ArrayList<>();
        for(int i = 1; i<sheet.getRows();i++) {

            String buggyNum = sheet.getCell(0, i).getContents();
            String correctNum = sheet.getCell(2, i).getContents();
            ProgramBuilder buggyP = new ProgramBuilder(Paths.get(PROBLEM_FORMAT_FOLDER.toString(), "wrong", buggyNum, "src").toString(),
                    CLASS_File_LIST[PROBLEM_NO - 1]);
            ProgramBuilder correctP = new ProgramBuilder(Paths.get(PROBLEM_FORMAT_FOLDER.toString(), "correct", correctNum, "src").toString(),
                    CLASS_File_LIST[PROBLEM_NO - 1]);
            System.out.println(buggyNum);
//            if(!buggyNum.equals("020"))
//                continue;
            long startTime = System.currentTimeMillis();
            EditScript editScript = new EditScript(buggyP.getTargetMethodDeclaration(TARGET_METHOD_LIST[PROBLEM_NO - 1]),
                    correctP.getTargetMethodDeclaration(TARGET_METHOD_LIST[PROBLEM_NO - 1]), true);
            long endTime = System.currentTimeMillis();
            CompilationUnit refactoredCorrectCM = getRefactoredCM(correctP, correctP.getTargetMethodDeclaration(TARGET_METHOD_LIST[PROBLEM_NO - 1]), TARGET_METHOD_LIST[PROBLEM_NO - 1]);
            CompilationUnit refactoredWrongCM = getRefactoredCM(buggyP, buggyP.getTargetMethodDeclaration(TARGET_METHOD_LIST[PROBLEM_NO - 1]), TARGET_METHOD_LIST[PROBLEM_NO - 1]);
            buggyP.toPath(Paths.get(OUTPUT_FOLDER, buggyNum, "wrong", "src").toString(), refactoredWrongCM);
            correctP.toPath(Paths.get(OUTPUT_FOLDER, buggyNum, "correct", "src").toString(), refactoredCorrectCM);
            buggyPs.add(buggyP);
            correctPs.add(correctP);
            buggyCSNums.add(editScript.getSrcNum());
            correctNums.add(editScript.getDstNum());
            editDist.add(editScript.getSrcEdit());
            BR_time.add((endTime - startTime) / 1000.0);

            Callable<Double> call = () -> {
                ProgramBuilder buggyP2 = new ProgramBuilder(Paths.get(FORMAT_FOLDER.toString(), "wrong", buggyNum, "src").toString(),
                        CLASS_File_LIST[PROBLEM_NO - 1]);
                ProgramBuilder correctP2 = new ProgramBuilder(Paths.get(FORMAT_FOLDER.toString(), "correct", correctNum, "src").toString(),
                        CLASS_File_LIST[PROBLEM_NO - 1]);
                long startTime1 = System.currentTimeMillis();
                EditScript editScript1 = new EditScript(buggyP2.getTargetMethodDeclaration(TARGET_METHOD_LIST[PROBLEM_NO - 1]),
                        correctP2.getTargetMethodDeclaration(TARGET_METHOD_LIST[PROBLEM_NO - 1]), false);
                long endTime1 = System.currentTimeMillis();
//                System.out.println((endTime1 - startTime1)/1000.0);
                return (endTime1 - startTime1) / 1000.0;
            };
            ExecutorService exec = Executors.newFixedThreadPool(1);
            Future<Double> future = exec.submit(call);
            try {
                Double c = future.get(1000 * 10, TimeUnit.MILLISECONDS);
                isBROSuccess.add(true);
                BRO_time.add(c);
            } catch (TimeoutException e) {
                future.cancel(true);
                isBROSuccess.add(false);
                BRO_time.add(10.0);
            }finally {
                future.cancel(true);
                try {
                    exec.shutdownNow();
                    if (!exec.awaitTermination(500, TimeUnit.MILLISECONDS)) { //超时后直接关闭
                        exec.shutdownNow();
                    }
                } catch (InterruptedException e) { //awaitTermination 出现中断异常也将触发关闭
                    exec.shutdownNow();
                    Thread.currentThread().interrupt();
                    Thread.currentThread().stop();
                }
            }

        }
        write2ExcelCFGuider("BR_TimeCompareFor",buggyPs,correctPs,buggyCSNums,correctNums,editDist,BR_time,BRO_time,isBROSuccess);
    }



    public static void repairWithoutFL() throws Exception {
        String methodToFix = TARGET_METHOD_LIST[PROBLEM_NO -1] + "@" + CLASS_File_LIST[PROBLEM_NO -1];
        String testSourceDir =TESTER_FOLDER.toString();
        String testClass = TESTER_CLASS_LIST[PROBLEM_NO -1];
        List<String> repairedCodes = new ArrayList<>();
        List<String> buggyFolders = new ArrayList<>();
        List<String> buggyCodes = new ArrayList<>();
        List<String> correctFolders = new ArrayList<>();
        List<String> correctCodes = new ArrayList<>();
        List<String> repairTime = new ArrayList<>();
        List<String> executionTime = new ArrayList<>();
        List<String> RRS = new ArrayList<>();
        File outPutFolder = new File(OUTPUT_FOLDER);
        File[] wrongFolders = outPutFolder.listFiles();
        if(wrongFolders == null)
            return ;
        boolean[] isFixed = new boolean[wrongFolders.length];
        int j=0;
        for (int i = 0; i < wrongFolders.length; i++) {
//            char c = 0;
//            System.out.println(c);
            // if(!wrongFolders[i].getName().equals("010"))
            //     continue;
            System.out.println("----------------------------------------------------------File  " + wrongFolders[i].getName() +
                    "----------------------------------------------------------");
            if (new File("tmp").exists())
                FileUtils.cleanDirectory(new File("tmp"));
            buggyFolders.add(wrongFolders[i].getName());
            Path wrongPath= Path.of(wrongFolders[i].getPath(),"wrong", "src",JAVA_FILE_LIST[PROBLEM_NO-1]);
            buggyCodes.add(Files.readString(wrongPath, StandardCharsets.UTF_8));
            correctFolders.add(wrongFolders[i].getName());
            Path correctPath= Path.of(wrongFolders[i].getPath(), "correct", "src",JAVA_FILE_LIST[PROBLEM_NO-1]);
            correctCodes.add(Files.readString(correctPath));
            String buggySourceDir = Paths.get(wrongFolders[i].getPath(), "wrong", "src").toString();
            String correctSourceDir = Paths.get(wrongFolders[i].getPath(), "correct", "src").toString();

            String[] cmdArgs = String.format("--BuggyProgramSourceDir %s --CorrectProgramSourceDir %s " +
                    "--MethodToFix %s --ProgramTestSourceDir %s --ProgramTestClass %s", buggySourceDir, correctSourceDir, methodToFix, testSourceDir, testClass).split(" ");
            CommandLine commandLine = parseCommandLine(CmdOptions.getCmdOptions(), cmdArgs);
            ConfigBuilder configBuilder = new ConfigBuilder();
            configBuilder.buildConfig(commandLine);
//            Fixer fixer = new Fixer();
            FixerWO fixer = new FixerWO();
            long startTime = System.currentTimeMillis();
            String fixCode = fixer.execute(configBuilder);
            long endTime = System.currentTimeMillis();
            double repair_time = (endTime-startTime)/1000.0 -fixer.getExecute_time();
            repairTime.add(Double.toString(repair_time));
            executionTime.add(Double.toString(fixer.getExecute_time()));
            VariableMatch.clearPreMatch();
            repairedCodes.add(fixCode);
            ASTEditDistanceCalculator astEditDistanceCalculator = new ASTEditDistanceCalculator(
                    buggyCodes.get(j),repairedCodes.get(j) , TARGET_METHOD_LIST[PROBLEM_NO-1], TARGET_METHOD_LIST[PROBLEM_NO-1]);
            float RPS=((float)astEditDistanceCalculator.getDistance())/
                    ((float)astEditDistanceCalculator.buggyASTSize);
            RRS.add(Double.toString(RPS));
            isFixed[j] = fixer.getFixResult().getResult();
            j++;
            System.out.println("----------------------------------------------------------File  " + wrongFolders[i].getName() +
                    "----------------------------------------------------------");
            System.out.println(repair_time);
            System.out.println();
        }
        write2Excel("RepairWOFLResult", buggyFolders, buggyCodes, correctFolders, correctCodes, repairedCodes, isFixed, repairTime, executionTime, RRS);
    }

    public static void repairWithFL() throws Exception {
        String methodToFix = TARGET_METHOD_LIST[PROBLEM_NO -1] + "@" + CLASS_File_LIST[PROBLEM_NO -1];
        String testSourceDir =TESTER_FOLDER.toString();
        String testClass = TESTER_CLASS_LIST[PROBLEM_NO -1];
        List<String> repairedCodes = new ArrayList<>();
        List<String> buggyFolders = new ArrayList<>();
        List<String> buggyCodes = new ArrayList<>();
        List<String> correctFolders = new ArrayList<>();
        List<String> correctCodes = new ArrayList<>();
        List<String> repairTime = new ArrayList<>();
        List<String> executionTime = new ArrayList<>();
        List<String> RRS = new ArrayList<>();
        File outPutFolder = new File(OUTPUT_FOLDER);
        File[] wrongFolders = outPutFolder.listFiles();
        if(wrongFolders == null)
            return ;
        boolean[] isFixed = new boolean[wrongFolders.length];
        int j=0;
        for (int i = 0; i < wrongFolders.length; i++) {
//            char c = 0;
//            System.out.println(c);
//            if(!wrongFolders[i].getName().equals("037"))
//                continue;
            System.out.println("----------------------------------------------------------File  " + wrongFolders[i].getName() +
                    "----------------------------------------------------------");
            if (new File("tmp").exists())
                FileUtils.cleanDirectory(new File("tmp"));
            buggyFolders.add(wrongFolders[i].getName());
            Path wrongPath= Path.of(wrongFolders[i].getPath(),"wrong", "src",JAVA_FILE_LIST[PROBLEM_NO-1]);
            buggyCodes.add(Files.readString(wrongPath, StandardCharsets.UTF_8));
            correctFolders.add(wrongFolders[i].getName());
            Path correctPath= Path.of(wrongFolders[i].getPath(), "correct", "src",JAVA_FILE_LIST[PROBLEM_NO-1]);
            correctCodes.add(Files.readString(correctPath));
            String buggySourceDir = Paths.get(wrongFolders[i].getPath(), "wrong", "src").toString();
            String correctSourceDir = Paths.get(wrongFolders[i].getPath(), "correct", "src").toString();

            String[] cmdArgs = String.format("--BuggyProgramSourceDir %s --ReferenceProgramSourceDir %s " +
                    "--MethodToFix %s --ProgramTestSourceDir %s --ProgramTestClass %s", buggySourceDir, correctSourceDir, methodToFix, testSourceDir, testClass).split(" ");
            CommandLine commandLine = parseCommandLine(CmdOptions.getCmdOptions(), cmdArgs);
            ConfigBuilder configBuilder = new ConfigBuilder();
            configBuilder.buildConfig(commandLine);
            Fixer fixer = new Fixer();
            long startTime = System.currentTimeMillis();
            String fixCode = fixer.execute(configBuilder.getConfig());
            long endTime = System.currentTimeMillis();
            double repair_time = (endTime-startTime)/1000.0 -fixer.getExecute_time();
            repairTime.add(Double.toString(repair_time));
            executionTime.add(Double.toString(fixer.getExecute_time()));
            VariableMatch.clearPreMatch();
            repairedCodes.add(fixCode);
            ASTEditDistanceCalculator astEditDistanceCalculator = new ASTEditDistanceCalculator(
                    buggyCodes.get(j),repairedCodes.get(j) , TARGET_METHOD_LIST[PROBLEM_NO-1], TARGET_METHOD_LIST[PROBLEM_NO-1]);
            float RPS=((float)astEditDistanceCalculator.getDistance())/
                    ((float)astEditDistanceCalculator.buggyASTSize);
            RRS.add(Double.toString(RPS));
            isFixed[j] = fixer.getFixResult().getResult();
            j++;
            System.out.println("----------------------------------------------------------File  " + wrongFolders[i].getName() +
                    "----------------------------------------------------------");
            System.out.println(repair_time);
            System.out.println();
        }
        write2Excel("RepairWFLResult", buggyFolders, buggyCodes, correctFolders, correctCodes, repairedCodes, isFixed, repairTime, executionTime, RRS);
    }

    private static void write2Excel(String dsc, List<String> buggyFolders, List<String> buggyCodes, List<String> correctFolders, List<String> correctCodes,
                                    List<String> repairedCodes, boolean[] isFixed, List<String> repairTime, List<String> executionTime, List<String> RRS) throws IOException, WriteException, BiffException {
        File file = new File(Paths.get(OUTPUT_PATH, String.format(dsc+"Problem%02d.xls",PROBLEM_NO)).toString());
        Workbook preWorkbook = null;
        WritableWorkbook workbook;
        if(!file.exists()){
            if(file.createNewFile()){

            }else{
                System.out.printf("Failed to create new file %s!", file.getPath());
                return;
            }
        }else{
            preWorkbook = Workbook.getWorkbook(file);
        }

        if(preWorkbook !=null){
            workbook = Workbook.createWorkbook(file, preWorkbook);
        }else{
            workbook = Workbook.createWorkbook(file);
        }
        WritableSheet sheet;
        Label label;
        if(preWorkbook!=null) {
            sheet = workbook.getSheet(0);
        }else {
            sheet = workbook.createSheet(dsc, 0);
            String[] titles;
            titles = new String[]{"Buggy_No", "Buggy_code", "Correct_No", "Correct_code", "Repaired_code", "Repair_time", "Execution_time", "RRS", "is_fixed"};
            for (int i = 0; i < titles.length; i++) {
                label = new Label(i, 0, titles[i]);
                sheet.addCell(label);
            }
        }
        int temp = sheet.getRows();
        for (int i = 0; i < buggyFolders.size(); i++) {
            label = new Label(0, i+temp, buggyFolders.get(i));
            sheet.addCell(label);
            label = new Label(1, i+temp, buggyCodes.get(i));
            sheet.addCell(label);
            label = new Label(2, i+temp, correctFolders.get(i));
            sheet.addCell(label);
            label = new Label(3, i+temp, correctCodes.get(i));
            sheet.addCell(label);
            label = new Label(4, i+temp, repairedCodes.get(i));
            sheet.addCell(label);
            label = new Label(5, i+temp, repairTime.get(i));
            sheet.addCell(label);
            label = new Label(6, i+temp, executionTime.get(i));
            sheet.addCell(label);
            label = new Label(7, i+temp, RRS.get(i));
            sheet.addCell(label);
            label = new Label(8, i+temp, Boolean.toString(isFixed[i]));
            sheet.addCell(label);
        }
        workbook.write();
        workbook.close();
    }


    public static void statistics() throws IOException, WriteException {
        ArrayList<ProgramBuilder> correctPs = getProgramsFromFolder(CORRECT_FOLDER.toString(),CLASS_File_LIST[PROBLEM_NO-1],TARGET_METHOD_LIST[PROBLEM_NO-1]);
        ArrayList<ProgramBuilder> buggyPs = getProgramsFromFolder(WRONG_FOLDER.toString(),CLASS_File_LIST[PROBLEM_NO-1],TARGET_METHOD_LIST[PROBLEM_NO-1]);
        File file = new File(Paths.get(OUTPUT_PATH, String.format("StatisticsFor"+"Problem%02d.xls",PROBLEM_NO)).toString());
        if(!file.exists()&&!file.createNewFile()){
            System.out.printf("Failed to create new file %s!", file.getPath());
            return;
        }
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet(String.format("StatisticsFor"+"Problem%02d",PROBLEM_NO), 0);
        String[] titles;
        titles = new String[]{"P_No", "LOC", "CFNodes"};
        Label label;
        for (int i = 0; i < titles.length; i++) {
            label = new Label(i, 0, titles[i]);
            sheet.addCell(label);
        }
        for (int i = 0; i < correctPs.size(); i++) {
            label = new Label(0, i+1, "C"+getFolderNum(correctPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(1, i+1, Integer.toString(correctPs.get(i).getMethodByName(TARGET_METHOD_LIST[PROBLEM_NO-1]).getMethodLOC()));
            sheet.addCell(label);
            CSNode csNode = new CSNode(CSNode.getBlockNodeFromFW(correctPs.get(i).getTargetMethodDeclaration(TARGET_METHOD_LIST[PROBLEM_NO-1])),
                    0, CSNode.CSType.METHOD_DECLARATION, null, 0);
            label = new Label(2, i+1, Integer.toString(CSNode.getCount(csNode,0)));
            sheet.addCell(label);
        }
        for (int i = 0; i < buggyPs.size(); i++) {
            label = new Label(0, correctPs.size()+i+1, "B"+getFolderNum(buggyPs.get(i).getClassPath()));
            sheet.addCell(label);
            label = new Label(1, correctPs.size()+i+1, Integer.toString(buggyPs.get(i).getMethodByName(TARGET_METHOD_LIST[PROBLEM_NO-1]).getMethodLOC()));
            sheet.addCell(label);
            CSNode csNode = new CSNode(CSNode.getBlockNodeFromFW(buggyPs.get(i).getTargetMethodDeclaration(TARGET_METHOD_LIST[PROBLEM_NO-1])),
                    0, CSNode.CSType.METHOD_DECLARATION, null, 0);
            label = new Label(2, correctPs.size()+i+1, Integer.toString(CSNode.getCount(csNode,0)));
            sheet.addCell(label);
        }
        workbook.write();
        workbook.close();
    }

    public static void evaluateChatGPT() throws IOException, WriteException {
//        ArrayList<ProgramBuilder> buggyPs = getProgramsFromWrongFolder(CHATGPT_PROBLEM_FOLDER.toString(), CLASS_File_LIST[PROBLEM_NO-1]);
        ArrayList<ProgramBuilder> buggyPs = getProgramsFromFolder(CHATGPT_PROBLEM_FOLDER.toString(), CLASS_File_LIST[PROBLEM_NO-1],TARGET_METHOD_LIST[PROBLEM_NO-1]);
        TesterBuilder testerProgram = new TesterBuilder(TESTER_FOLDER.toString(), TESTER_CLASS_LIST[PROBLEM_NO-1]);
        ArrayList<Boolean> isRepaired = new ArrayList<>();
        for(ProgramBuilder buggyProgram:buggyPs) {
            JDIDebuggerExecutor.preCompile();
            JDIDebuggerExecutor.compileTest(buggyProgram, testerProgram);
            JDIDebuggerExecutor.executeAllTest(buggyProgram, testerProgram, TARGET_METHOD_LIST[PROBLEM_NO-1]);
            if(Fixer.isCompileError()){
                isRepaired.add(false);
                System.out.println("sdasdafd");
                System.out.println(buggyProgram.getClassPath());
            }
            else{
                TestResult testResult= new TestResult(LOG_PATH);
                if (testResult.getResult()) {
                    isRepaired.add(true);
                }
                else isRepaired.add(false);
            }
        }
        write2ExcelC("ChatGPTRepairResults", buggyPs, isRepaired);
    }

    public static void remove(String folderPath) throws IOException {
        File folder = new File(folderPath);
        if (!folder.isDirectory() || Objects.requireNonNull(folder.listFiles()).length == 0) {
            System.out.printf("Give a wrong folder to get programs of %s!\n", folderPath);
            return;
        }
        File[] files = folder.listFiles();
        if(files == null)
            return ;
        for (File file : files) {
            if (file.getPath().contains(".DS_Store") ||file.getPath().contains(".idea"))
                continue;
            System.out.println(file.getPath());
            String classPath1 = Paths.get(file.getPath(),"src",CLASS_File_LIST[PROBLEM_NO-1]+".txt").toString();
            String classPath = Paths.get(file.getPath(),"src",JAVA_FILE_LIST[PROBLEM_NO-1]).toString();
            File file1 = new File(classPath1);
            BufferedReader br = new BufferedReader(new FileReader(file1));

            FileWriter fileStream = new FileWriter(classPath);
            BufferedWriter out = new BufferedWriter(fileStream);
            String line;
            line = br.readLine();
            if(line.contains("```")) {
                while (!(line = br.readLine()).contains("```")) {
                    out.write(line + "\n");
                }
            }else{
                do{
                    out.write(line + "\n");
                }while((line=br.readLine())!=null);
            }
            out.close();
//           while((line=br.readLine())!=null) {
//               out.write(line+"\n");
//            }

        }
    }



    public static void main(String[] arg) throws Exception {
//        formatFolders();
//        ArrayList<ProgramBuilder> buggyPs = getProgramsFromFolder(WRONG_FOLDER.toString(), CLASS_File_LIST[PROBLEM_NO-1], TARGET_METHOD_LIST[PROBLEM_NO-1]);
//        ArrayList<ProgramBuilder> correctPs = getProgramsFromFolder(CORRECT_FOLDER.toString(), CLASS_File_LIST[PROBLEM_NO-1], TARGET_METHOD_LIST[PROBLEM_NO-1]);
//        initMethod2FixForPrograms(buggyPs, TARGET_METHOD_LIST[PROBLEM_NO-1]);
//        initMethod2FixForPrograms(correctPs, TARGET_METHOD_LIST[PROBLEM_NO-1]);
//        List<String> wrongFolders = outputSameCFSPairs(buggyPs, correctPs);
//        searchByCFSandAST(buggyPs, correctPs, wrongFolders);
        outPutCFGuider();
        repairWithoutFL();
//        repairWithFL();
//        evaluateChatGPT();
//        remove(CHATGPT_WRONG_FOLDER.toString());
    }
}
