package program;

import cfs.CFSVisitor;
import cfs.Format;
import com.github.gumtreediff.gen.javaparser.JavaParserGenerator;
import com.github.gumtreediff.tree.TreeContext;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class ProgramBuilder {
    private final String fileName;//HelloWorld.java
    private final String parentPath;//src/main/java/monitor
    private final String classPath;//src/main/java
    private final String className;//monitor.HelloWorld
    private String packageName;
    private final Map<String, MethodBuilder> methods;
    private CompilationUnit compilationUnit;
    private Map<String, MethodDeclaration> methodDeclarationMap;
    private CFSVisitor methodToFixCFS;
    private Map<String, ImportDeclaration> importDeclarationMap;
    private TreeContext treeContext;

    public String getFilePath(){
        return Paths.get(parentPath,fileName).toString();
    }

    public String getParentPath() {
        return parentPath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getClassName() {
        return className;
    }

    public String getClassPath() {
        return classPath;
    }

    public static ArrayList<ProgramBuilder> getProgramBuilders(String folderPath, String className) throws IOException {
        ArrayList<ProgramBuilder> programBuilders = new ArrayList<>();
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
            ProgramBuilder programBuilder = new ProgramBuilder(classPath,className);
            programBuilders.add(programBuilder);
        }
        return programBuilders;
    }

    public Boolean formatTargetMethod(String methodName, String newFilePath) throws IOException {
        MethodDeclaration targetMethodD = this.getMethodDeclarationMap().get(methodName);
        if(targetMethodD == null) {
            System.out.printf("error parser file %s%n", Paths.get(this.parentPath, this.fileName));
            return false;
        }
        else {
            Format.formatProgram(targetMethodD);
            this.toPath(newFilePath);
            return true;
        }
    }

    public ProgramBuilder(String sourceDir, String className) throws IOException {
        String codePath = setPackageNameFromClass(sourceDir, className);
        this.fileName = className.substring(className.lastIndexOf(".")+1) + ".java";
        this.classPath = sourceDir;
        this.parentPath = codePath;
        this.className = className;
        this.methods = new HashMap<>();
        initCompilation();
    }

    public TreeContext getTreeContext() {
        return treeContext;
    }

    private void initCompilation() throws IOException {
        String filePath = getFilePath();
        JavaParser javaParser=new JavaParser();
        this.methodDeclarationMap = new HashMap<>();
        this.compilationUnit = null;
        this.treeContext = new JavaParserGenerator().generateFrom().file(filePath);
        if(javaParser.parse(new File(filePath)).getResult().isPresent()) {
            this.compilationUnit = javaParser.parse(new File(filePath)).getResult().get();
            for (MethodDeclaration methodDeclaration : compilationUnit.findAll(MethodDeclaration.class)) {
                this.methodDeclarationMap.put(methodDeclaration.getName().getIdentifier(), methodDeclaration);
            }
        }

        this.importDeclarationMap = new HashMap<>();
        for(ImportDeclaration importDeclaration:compilationUnit.findAll(ImportDeclaration.class)){
//            System.out.println(importDeclaration.getName());
            this.importDeclarationMap.put(importDeclaration.getName().getIdentifier(), importDeclaration);
        }
    }

    public Map<String, ImportDeclaration> getImportDeclarationMap() {
        return importDeclarationMap;
    }

    public CFSVisitor getMethodToFixCFS() {
        return methodToFixCFS;
    }

    public void setMethodToFixCFS(CFSVisitor methodToFixCFS) {
        this.methodToFixCFS = methodToFixCFS;
    }

    private String setPackageNameFromClass(String sourceDir, String className){
        String codePath = sourceDir;
        packageName = "";
        if(className.contains(".")) {
            this.packageName = className.substring(0, className.lastIndexOf("."));
            codePath = Paths.get(sourceDir, packageName.split("\\.")).toString();
        }
        return codePath;
    }

    public Map<String, MethodDeclaration> getMethodDeclarationMap() {
        return methodDeclarationMap;
    }

    public MethodDeclaration getTargetMethodDeclaration(String methodName){
        return methodDeclarationMap.get(methodName);
    }

    public void initMethodBuilder(String methodToFix){
        this.methods.put(methodToFix, new MethodBuilder(this.methodDeclarationMap.get(methodToFix)));
        this.methods.get(methodToFix).setProgramBuilder(this);
    }


    public MethodBuilder getMethodByName(String name){
        return methods.get(name);
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
    //    public void monitorProgramStatus(String testerPath, String testMethod){
//        JDIDebuggerExecutor.executeTest(testerPath,this.filePath,);
//    }

    public void toPath(String filePath) throws IOException {
        toPath(filePath, this.compilationUnit);
    }

    public void toPath(String filePath, CompilationUnit cm) throws IOException {
        filePath = Paths.get(filePath, this.packageName.split("\\.")).toString();
        File folder = new File(filePath);
        if(!folder.exists()&&!folder.mkdirs()){
            System.out.printf("Failed to mkdirs %s!", filePath);
            return;
        }
        File file = new File(Paths.get(filePath, this.fileName).toString());
        if(!file.exists()&&!file.createNewFile()) {
            System.out.printf("Failed to create new file %s!", file.getPath());
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(cm.toString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean containUnConstraintMethod(String methodName, ProgramBuilder pb1, ProgramBuilder pb2){
        if(pb1.methods.containsKey(methodName) && pb2.methods.containsKey(methodName))
            return false;
        return !pb1.methods.containsKey(methodName) && pb2.methods.containsKey(methodName);
    }


    private static class MethodNamePrinter extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);
            System.out.println("Method Name Printed: " + md.getName());
        }
    }
}
