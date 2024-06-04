package evaluate;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Optional;

public class CodeRowsCalculator {
    private int rows;
    public CodeRowsCalculator(String code,String methodname){
        Optional<CompilationUnit>compilationUnit=ASTGenerator.getCU(code);
        compilationUnit.ifPresent(c->{
            for (MethodDeclaration m : c.findAll(MethodDeclaration.class)) {
                if (m.getName().getIdentifier().equals(methodname)) {
                    assert (m.getEnd().isPresent()&&m.getBegin().isPresent());
                    rows = m.getEnd().get().line - m.getBegin().get().line + 1;
                    break;
                }
            }
        });
    }
    public int getRows() {
        return rows;
    }
}
