package program;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TesterBuilder {
    String javaName;//xxxTest.java
    String javaParentPath;//.../package/

    String className;//package.xxxTest
    String classPath;//..

    Map<String, String> testList;
    List<String> testerNames;
    public TesterBuilder(String sourceDir,String className) throws FileNotFoundException {
        String packageName;
        String codePath = sourceDir;
        if(className.contains(".")) {
            packageName = className.substring(0, className.lastIndexOf("."));
            codePath = Paths.get(sourceDir, packageName.split("\\.")).toString();
        }

        this.javaParentPath = codePath;
        this.javaName = className.substring(className.lastIndexOf(".")+1) + ".java";
        this.classPath = sourceDir;
        this.className = className;
        testList = new HashMap<>();
        testerNames=new ArrayList<>();
        String fileName=className.replace('.','/');
        setTesterNames(sourceDir+"/"+fileName+".java");
    }

    public String getFilePath(){
        return Paths.get(javaParentPath,javaName).toString();
    }

    public String getJavaParentPath() {
        return javaParentPath;
    }

    public String getJavaName() {
        return javaName;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getClassName() {
        return className;
    }
    public void setTesterNames(String filePath) throws FileNotFoundException {
        JavaParser javaParser=new JavaParser();
        if(javaParser.parse(new File(filePath)).getResult().isPresent()) {
            CompilationUnit compilation = javaParser.parse(new File(filePath)).getResult().get();
            for (MethodDeclaration methodDeclaration : compilation.findAll(MethodDeclaration.class)) {
                testerNames.add(methodDeclaration.getName().toString());
            }
        }
    }

    public List<String> getTesterNames() {
        return testerNames;
    }
}
