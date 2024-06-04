package evaluate;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.util.Optional;

public class ASTGenerator {

//    public static Optional<CompilationUnit> getCU(String filepath) throws FileNotFoundException {
//        String absolutePath = new File("").getAbsolutePath();
//        Path path = Paths.get(absolutePath, filepath);
//        JavaParser javaParser = new JavaParser();
//        return javaParser.parse(new File(path.toString())).getResult();
//    }
    public static Optional<CompilationUnit> getCU(String code) {
        JavaParser javaParser = new JavaParser();
        return javaParser.parse(code).getResult();
    }
}
