package sar;

import cfs.CFSVisitor;
import cfs.guider.EditScript;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import program.ProgramBuilder;
import sar.config.Config;
import sar.config.ConfigBuilder;

import java.nio.file.Paths;

import static cfs.guider.CFGuider.getRefactoredCM;

public class BiRefactor {
    protected ProgramBuilder buggyProgram;
    protected ProgramBuilder correctProgram;
    protected String methodToFix;

    public void execute(Config config) throws Exception {
        buggyProgram = config.getBuggyProgram();
        correctProgram = config.getReferenceProgram();
        methodToFix = config.getFaultyMethodSignature();
        buggyProgram.initMethodBuilder(methodToFix);
        correctProgram.initMethodBuilder(methodToFix);
        MethodDeclaration targetMethodD = buggyProgram.getMethodDeclarationMap().get(methodToFix);
        CFSVisitor buggyCFS = new CFSVisitor(targetMethodD);
        CFSVisitor correctCFS = new CFSVisitor(correctProgram.getMethodDeclarationMap().get(methodToFix));
        if(buggyCFS.getStrCFS().equals(correctCFS.getStrCFS())) {
            System.out.println("They hold same control-flow structures.");
            return;
        }
        System.out.println("They hold different control-flow structures.");
        System.out.println("================================================Begin bidirectional refactoring....================================================");
        long startTime = System.currentTimeMillis();
        EditScript editScript = new EditScript(buggyProgram.getTargetMethodDeclaration(methodToFix),
                correctProgram.getTargetMethodDeclaration(methodToFix), false);
        long endTime = System.currentTimeMillis();
        System.out.println("================================================End bidirectional refactoring....================================================");
        System.out.printf("Time cost:%s ms\n",endTime-startTime);
        CompilationUnit refactoredCorrectCM = getRefactoredCM(correctProgram, correctProgram.getTargetMethodDeclaration(methodToFix),methodToFix);
        CompilationUnit refactoredWrongCM = getRefactoredCM(buggyProgram, buggyProgram.getTargetMethodDeclaration(methodToFix), methodToFix);
        String newWrongFolder = Paths.get("output","refactored", "wrong", "src").toString();
        String newCorrectFolder = Paths.get("output", "refactored", "correct", "src").toString();
        buggyProgram.toPath(newWrongFolder, refactoredWrongCM);
        correctProgram.toPath(newCorrectFolder, refactoredCorrectCM);
        config.setBuggyProgram(new ProgramBuilder(newWrongFolder, buggyProgram.getClassName()));
        config.setReferenceProgram(new ProgramBuilder(newCorrectFolder, correctProgram.getClassName()));
    }
}
