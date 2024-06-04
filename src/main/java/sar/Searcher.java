package sar;

import ast.AstDiff;
import ast.JdtTreeContext;
import cfs.CFSVisitor;
import cfs.guider.CFBuilder;
import program.ProgramBuilder;

import java.io.IOException;
import java.util.ArrayList;

import static cfs.guider.CFGuider.controlFlowGuide;
import static cfs.guider.CFGuider.findKthClosestPrograms;

public class Searcher {
    public static ArrayList<ProgramBuilder> searchBySameCFS(ProgramBuilder buggyProgram, String targetMethod, ArrayList<ProgramBuilder> correctPrograms){
        CFSVisitor buggyMethodVisitor = new CFSVisitor(buggyProgram.getTargetMethodDeclaration(targetMethod));
        ArrayList<ProgramBuilder> correctProgramsWithSameCFS = new ArrayList<>();
        for(ProgramBuilder correctProgram : correctPrograms){
            CFSVisitor correctMethodVisitor = new CFSVisitor(correctProgram.getTargetMethodDeclaration(targetMethod));
            if (buggyMethodVisitor.getStrCFS().equals(correctMethodVisitor.getStrCFS())){
                correctProgramsWithSameCFS.add(correctProgram);
            }
        }
        return correctProgramsWithSameCFS;
    }

    public static ProgramBuilder searchByAST(ProgramBuilder buggyProgram, String targetMethod, ArrayList<ProgramBuilder> correctPrograms){
        double dist = 10000000;
        if (correctPrograms.size() == 0) {
            return null;
        }
        ProgramBuilder tempProgram = correctPrograms.get(0);
        for(ProgramBuilder correctProgram : correctPrograms){
            double temp = AstDiff.myCompute(buggyProgram.getTreeContext(), correctProgram.getTreeContext()).astSimilarity(targetMethod);
            if (temp < dist) {
                dist = temp;
                tempProgram = correctProgram;
            }
        }
        return tempProgram;
    }

    public static ProgramBuilder searchByCFS(ProgramBuilder buggyProgram, String targetMethod, ArrayList<ProgramBuilder> correctPrograms) throws IOException {
        ArrayList<CFBuilder> kthClosestPrograms = findKthClosestPrograms(buggyProgram, correctPrograms, targetMethod);
        return controlFlowGuide(buggyProgram, kthClosestPrograms,targetMethod);
    }

    public static ProgramBuilder search(ProgramBuilder buggyProgram, String targetMethod, ArrayList<ProgramBuilder> correctPrograms) throws IOException {
        ArrayList<ProgramBuilder> correctProgramsWithSameCFS = searchBySameCFS(buggyProgram, targetMethod, correctPrograms);
        if(correctProgramsWithSameCFS.size()==0){
            return searchByCFS(buggyProgram, targetMethod, correctPrograms);
        }
        else{
            return searchByAST(buggyProgram, targetMethod,correctPrograms);
        }
    }
}
