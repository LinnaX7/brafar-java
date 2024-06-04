package sar;

import program.ProgramBuilder;
import sar.config.Config;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class SBrafar {
    protected String methodToFix;
    protected String newWrongSourceDir = Paths.get("output", "wrong", "src").toString();
    protected String newCorrectSourcesDir = Paths.get("output", "correct").toString();
    protected String searchedReferenceDir = Paths.get("output", "reference","src").toString();

    public Boolean formatProgram(Config config) throws IOException {
        ProgramBuilder originalBuggyProgram = config.getBuggyProgram();
        ArrayList<ProgramBuilder> originalCorrectPrograms = config.getCorrectPrograms();
        this.methodToFix = config.getFaultyMethodSignature();
        Boolean formatResult1 = originalBuggyProgram.formatTargetMethod(this.methodToFix, this.newWrongSourceDir);
        if(!formatResult1){
            return false;
        }
        for (ProgramBuilder originalCorrectProgram : originalCorrectPrograms) {
            String correctNum = Paths.get(originalCorrectProgram.getClassPath()).getParent().getFileName().toString();
            originalCorrectProgram.formatTargetMethod(this.methodToFix, Paths.get(newCorrectSourcesDir, correctNum, "src").toString());
        }
        return true;
    }

    public void execute(Config config) throws Exception {
        System.out.println("===========================Begin formatting===========================");
        Boolean formatResult = formatProgram(config);
        if(!formatResult){
            System.out.println("Repair failed, no target method to fix!");
            return;
        }
        System.out.println("===========================End formatting===========================");
        ProgramBuilder buggyProgram = new ProgramBuilder(this.newWrongSourceDir, config.getClassName());
        config.setBuggyProgram(buggyProgram);
        ArrayList<ProgramBuilder> correctPrograms = ProgramBuilder.getProgramBuilders(newCorrectSourcesDir, config.getClassName());
        config.setCorrectPrograms(correctPrograms);
        System.out.println("===========================Begin searching the best reference program===========================");
        ProgramBuilder correctProgram = Searcher.search(buggyProgram, config.getFaultyMethodSignature(), correctPrograms);
        System.out.println("===========================End searching the best reference program===========================");
        System.out.printf("The best reference program is:%s\n", correctProgram.getFilePath());
//        correctProgram.toPath(searchedReferenceDir);
        config.setReferenceProgram(correctProgram);
        BiRefactor biRefactor = new BiRefactor();
        biRefactor.execute(config);
        Fixer fixer = new Fixer();
        fixer.execute(config);
    }

}
