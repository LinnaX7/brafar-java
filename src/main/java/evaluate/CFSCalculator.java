package evaluate;

import cfs.CFSVisitor;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.Optional;

public class CFSCalculator {
    private int cfs;
    public CFSCalculator(String code,String methodname){
        Optional<CompilationUnit> compilationUnit=ASTGenerator.getCU(code);
        compilationUnit.ifPresent(c->{
            for (MethodDeclaration m : c.findAll(MethodDeclaration.class)) {
                if (m.getName().getIdentifier().equals(methodname)) {
                    CFSVisitor cfsVisitor=new CFSVisitor(m);
                    cfs=cfsVisitor.getCfsNums();
                }
            }
        });
    }

    public int getCfs() {
        return cfs;
    }
}
