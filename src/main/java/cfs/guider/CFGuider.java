package cfs.guider;
import cfs.guider.mapping.LoopMapping;
import cfs.guider.mapping.RoughMapping;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import program.ProgramBuilder;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class CFGuider {

    public static int K = 5;


    public static ArrayList<CFBuilder> findKthClosestPrograms(ProgramBuilder buggyP, ArrayList<ProgramBuilder> correctPs, String targetMethod){
        ArrayList<CFBuilder> kthCorrectPs = new ArrayList<>();
        Set<CFBuilder> sortedCorrectPs = new TreeSet<>();
        CSNode buggyCS = new CSNode(buggyP.getTargetMethodDeclaration(targetMethod));
        for(ProgramBuilder correctP:correctPs) {
            CFBuilder cfBuilder = new CFBuilder(correctP, targetMethod);
            LoopMapping loopMapping = new LoopMapping(buggyCS, cfBuilder.getCsNode());
            loopMapping.mapping();
            cfBuilder.setLoopPair(loopMapping.getMappingScoreRate());
            RoughMapping roughMapping = new RoughMapping(buggyCS, cfBuilder.getCsNode());
            roughMapping.mapping();
            cfBuilder.setNodesCount(Math.abs(roughMapping.getDstNodes().size()-roughMapping.getSrcNodes().size()));
            cfBuilder.setRoughPair(roughMapping.getMappingScoreRate());
            sortedCorrectPs.add(cfBuilder);
        }
        for(CFBuilder cfBuilder:sortedCorrectPs){
            kthCorrectPs.add(cfBuilder);
            if(kthCorrectPs.size()==K)
                break;
        }
        return kthCorrectPs;
    }

    public static ProgramBuilder controlFlowGuide(ProgramBuilder buggyP, ArrayList<CFBuilder> kthClosestPrograms, String targetMethod) throws IOException {
        MethodDeclaration closestMD = null;
        MethodDeclaration refactoredBuggyM = null;
        ProgramBuilder closestPB = null;
        int srcEditDist = 100000;
        int dstEditDist = 100000;
        double maxPair = 0;
        double maxMapRate = 0.0;
        String wrongNum = Paths.get(buggyP.getClassPath()).getParent().getFileName().toString();
        System.out.printf("Generating control flow structure guidance of No.%s.\n", wrongNum);
        for(CFBuilder cfBuilder:kthClosestPrograms){
//            System.out.println(cfBuilder.getProgramBuilder().getFileName());
            MethodDeclaration buggyM = buggyP.getTargetMethodDeclaration(targetMethod).clone();
            MethodDeclaration correctM = cfBuilder.getProgramBuilder().getTargetMethodDeclaration(targetMethod).clone();
//            System.out.println(cfBuilder.getProgramBuilder().getClassPath());
            EditScript editScript = new EditScript(buggyM, correctM, true);
            if(editScript.flag){
                if(srcEditDist>=editScript.srcEdit){
                    if(dstEditDist > editScript.dstEdit){
                        srcEditDist = editScript.srcEdit;
                        dstEditDist = editScript.dstEdit;
                        closestMD = correctM;
                        closestPB = cfBuilder.getProgramBuilder();
                        maxPair = editScript.legalMapping.getMappingScore();
                        maxMapRate = editScript.legalMapping.getMapRate();
                        refactoredBuggyM = buggyM;
                        continue;
                    }
                }
                boolean isMax = false;
                if(editScript.legalMapping.getMapRate()>maxMapRate){
                    isMax = true;
                }else if(editScript.legalMapping.getMapRate() == maxMapRate){
                    if(editScript.legalMapping.getMappingScore()>maxPair)
                        isMax = true;
                }
                if(isMax){
                    srcEditDist = editScript.srcEdit;
                    dstEditDist = editScript.dstEdit;
                    closestMD = correctM;
                    closestPB = cfBuilder.getProgramBuilder();
                    maxPair = editScript.legalMapping.getMappingScore();
                    maxMapRate = editScript.legalMapping.getMapRate();
                    refactoredBuggyM = buggyM;
                }
            }
        }
//        String wrongNum = Paths.get(buggyP.getClassPath()).getParent().getFileName().toString();

        if(closestMD == null) {
            System.out.printf("Failed to Generate control flow structure guidance of No.%s!\n", wrongNum);
            return null;
        }
        return closestPB;

//        CompilationUnit refactoredCorrectCM = getRefactoredCM(closestPB, closestMD);
//        CompilationUnit refactoredWrongCM = getRefactoredCM(buggyP, refactoredBuggyM);
//        buggyP.toPath(Paths.get(OUTPUT_FOLDER, wrongNum, "wrong", "src").toString(), refactoredWrongCM);
//        closestPB.toPath(Paths.get(OUTPUT_FOLDER, wrongNum, "correct", "src").toString(), refactoredCorrectCM);

    }

    public static CompilationUnit getRefactoredCM(ProgramBuilder pb, MethodDeclaration newMD, String targetMethod){
        assert newMD.getBody().isPresent();
        CompilationUnit refactoredCM = pb.getCompilationUnit().clone();
        for(MethodDeclaration methodDeclaration:refactoredCM.findAll(MethodDeclaration.class)){
            if(methodDeclaration.getName().getIdentifier().equals(targetMethod)){
                methodDeclaration.setBody(newMD.getBody().get());
            }
        }
        return refactoredCM;
    }

}
