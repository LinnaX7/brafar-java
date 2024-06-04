package cfs.guider;

import cfs.guider.action.Action;
import cfs.guider.action.Insert;

import java.util.ArrayList;
import java.util.HashMap;
import cfs.guider.action.Move;
import cfs.guider.mapping.LegalMapping;
import cfs.guider.mapping.LocalMapping;
import com.github.javaparser.ast.body.MethodDeclaration;


public class EditScript {

    ArrayList<Action> srcActions = null;
    ArrayList<Action> dstActions = null;
    int dstEdit = 0;
    int srcEdit = 0;

    int srcNum = 0;
    int dstNum = 0;
    ArrayList<String> guidance;
    boolean flag;
    LegalMapping legalMapping;
    LocalMapping localMapping;
    private boolean isSuccess;

    public int getSrcEdit() {
        return srcEdit;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public int getSrcNum() {
        return srcNum;
    }

    public int getDstNum() {
        return dstNum;
    }

    private void initLegalMapping(MethodDeclaration buggyMethod, MethodDeclaration correctMethod, boolean isOptimal){
        CSNode buggyMNode = new CSNode(CSNode.getBlockNodeFromFW(buggyMethod), 0, CSNode.CSType.METHOD_DECLARATION, null, 0);
        CSNode correctMNode = new CSNode(CSNode.getBlockNodeFromFW(correctMethod), 0, CSNode.CSType.METHOD_DECLARATION, null, 0);

        legalMapping = new LegalMapping(buggyMNode, correctMNode, isOptimal);
        legalMapping.mapping();
        srcNum = legalMapping.getSrcNodes().size();
        dstNum = legalMapping.getDstNodes().size();
        ArrayList<CSNode> ifNodesToBeChanged = LegalMapping.getIfCSNodeToChangeBranch(legalMapping.getSrcToDst());
        for(CSNode ifCSNode:ifNodesToBeChanged){
            ifCSNode.branchChange();
        }
        if(ifNodesToBeChanged.size()!=0){
            legalMapping = new LegalMapping(buggyMNode, correctMNode, isOptimal);
            legalMapping.mapping();
        }
    }

    private void initLocalMapping(){
        localMapping = new LocalMapping(legalMapping.getSrc(), legalMapping.getDst(), legalMapping);
        ArrayList<CSNode> ifNodesToBeChanged = localMapping.getAllIfNodesToBeChanged();
        for(CSNode ifCSNode:ifNodesToBeChanged){
            ifCSNode.branchChange();
        }

        if(ifNodesToBeChanged.size()!=0)
            localMapping = new LocalMapping(legalMapping.getSrc(), legalMapping.getDst(), legalMapping);
    }

    public EditScript(MethodDeclaration buggyMethod, MethodDeclaration correctMethod, boolean isOptimal){
        this.isSuccess = false;
        this.flag = true;
        this.guidance = new ArrayList<>();
        initLegalMapping(buggyMethod, correctMethod, isOptimal);
        refactoringGuide();
        this.isSuccess = true;
    }

    public ArrayList<Action> unidirectionalRefactor(ArrayList<CSNode> srcNodes, HashMap<CSNode, CSNode> srcToDst, HashMap<CSNode,CSNode> dstToSrc){
        ArrayList<Action> actions = new ArrayList<>();
        for(CSNode srcNode:srcNodes){
            if(srcToDst.get(srcNode) == null){
                CSNode dstNodeP = srcToDst.get(srcNode.getParent());
                if( dstNodeP!= null){
                    switch (srcNode.getCsType()){
                        case IF_STMT:
                        case WHILE_STMT:
                        case FOR_STMT:
                        case FOREACH_STMT:
                            Insert insert = new Insert(dstNodeP, srcNode, dstToSrc, srcToDst);
                            actions.add(insert);
                            break;
                        case ELSE_BRANCH:
                        case THEN_BRANCH:
                            Insert insert1 = new Insert(dstNodeP, srcNode, dstToSrc, srcToDst);
                            new Move(insert1.getNewCSNode(), srcNode, srcToDst);
                            break;
                        default:
                            flag = false;
                    }
                }
            }
            if(!flag)
                return actions;
        }
        return actions;
    }

    private void refactoringGuideLocalMapping(){
//        LocalMapping localMapping = new LocalMapping(legalMapping.getSrc(), legalMapping.getDst(), legalMapping);
        this.dstActions.addAll(unidirectionalRefactor(localMapping.getSrcNodes(), legalMapping.getSrcToDst(), legalMapping.getDstToSrc()));
        if(!flag) {
//            System.out.println("Error");
//            return localMapping;
            return;
        }
        this.srcActions.addAll(unidirectionalRefactor(localMapping.getDstNodes(), legalMapping.getDstToSrc(), legalMapping.getSrcToDst()));
//        return localMapping;
    }

    private void refactoringGuide(){
        this.dstActions = unidirectionalRefactor(legalMapping.getSrcCSNodes(), legalMapping.getSrcToDst(), legalMapping.getDstToSrc());
        if(!flag) {
//            System.out.println("Error");
            return;
        }
        this.srcActions = unidirectionalRefactor(legalMapping.getDstCSNodes(), legalMapping.getDstToSrc(), legalMapping.getSrcToDst());
        if(!flag) {
//            System.out.println("Error");
            return;
        }
//        LocalMapping localMapping = refactoringGuideLocalMapping();
        initLocalMapping();
        refactoringGuideLocalMapping();
        if(!flag) {
//            System.out.println("Error");
            return;
        }
//        if(localMapping.getAllIfNodesToBeChanged().size()!=0){
//            for(CSNode ifCSNode: localMapping.getAllIfNodesToBeChanged())
//                ifCSNode.branchChange();
//            refactoringGuideLocalMapping();
//        }
        this.srcEdit = this.srcActions.size();
        this.dstEdit = this.dstActions.size();

    }

}
