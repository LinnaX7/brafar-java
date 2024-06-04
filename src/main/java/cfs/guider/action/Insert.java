package cfs.guider.action;

import cfs.Refactor;
import com.github.javaparser.ast.stmt.*;
import cfs.guider.CSNode;

import java.util.HashMap;

public class Insert extends Action{
    CSNode.CSType csType;
    int csIndex;
    int treeIndex;
    CSNode newCSNode = null;

    public Insert(CSNode srcParentCSNode,CSNode dstCSNode, HashMap<CSNode,CSNode> srcToDst, HashMap<CSNode,CSNode> dstToSrc) {
        super(ActionType.INSERT);
        this.csType = dstCSNode.getCsType();
        this.csIndex = 0;
        this.treeIndex = 0;
        if(dstCSNode.isBranch())
            insertCSBranch(srcParentCSNode, dstCSNode, srcToDst, dstToSrc);
        else
            insertCSNode(srcParentCSNode, dstCSNode, srcToDst, dstToSrc);
    }

    private void insertCSBranch(CSNode srcParentCSNode,CSNode dstCSNode, HashMap<CSNode,CSNode> srcToDst, HashMap<CSNode,CSNode> dstToSrc){
        switch (dstCSNode.getCsType()){
            case THEN_BRANCH:
                srcToDst.put(srcParentCSNode.getChildren().get(0), dstCSNode);
                dstToSrc.put(dstCSNode, srcParentCSNode.getChildren().get(0));
                this.newCSNode = srcParentCSNode.getChildren().get(0);
                break;
            case ELSE_BRANCH:
                BlockStmt newElse = new BlockStmt();
                newElse.addStatement(new EmptyStmt());
                ((IfStmt)srcParentCSNode.getTreeNode()).setElseStmt(newElse);
                CSNode elseBranchNode = new CSNode(newElse, srcParentCSNode.getHeight()+1, CSNode.CSType.ELSE_BRANCH, srcParentCSNode, 0);
                srcParentCSNode.getChildren().add(elseBranchNode);
                srcToDst.put(elseBranchNode, dstCSNode);
                dstToSrc.put(dstCSNode, elseBranchNode);
                this.newCSNode = elseBranchNode;
                break;
        }
    }

    private void insertCSNode(CSNode srcParentCSNode,CSNode dstCSNode, HashMap<CSNode,CSNode> srcToDst, HashMap<CSNode,CSNode> dstToSrc){
        int csIndex = dstCSNode.getParent().getChildren().indexOf(dstCSNode);
        int treeIndex=0;
        if(csIndex<0)
            System.out.println("error");
        if(csIndex != 0){
            CSNode leftNode = dstToSrc.get(dstCSNode.getParent().getChildren().get(csIndex-1));
            while(leftNode == null && (csIndex-2)>=0){
                csIndex = csIndex-1;
                leftNode = dstToSrc.get(dstCSNode.getParent().getChildren().get(csIndex-1));
            }
            while (leftNode!=null&&leftNode.getParent() != srcParentCSNode){
                leftNode = leftNode.getParent();
            }
            csIndex = srcParentCSNode.getChildren().indexOf(leftNode) + 1;
        }
        if(srcParentCSNode.getChildren().size()==0){
            csIndex = 0;
        }
        else if(srcParentCSNode.getChildren().size() <= csIndex){
            treeIndex = srcParentCSNode.getChildren().get(srcParentCSNode.getChildren().size()-1).getTIndex() +1;
            csIndex = srcParentCSNode.getChildren().size();
        }else {
            while ((csIndex<srcParentCSNode.getChildren().size() && !srcToDst.containsKey(srcParentCSNode.getChildren().get(csIndex)))
            && !srcParentCSNode.getChildren().get(csIndex).isHasDBeMatched()){
                csIndex+=1;
            }
            if(csIndex == srcParentCSNode.getChildren().size())
                treeIndex = srcParentCSNode.getChildren().get(srcParentCSNode.getChildren().size()-1).getTIndex() +1;
            else {
                int t1 = -1;
                if(csIndex >= 1){
                    t1 = srcParentCSNode.getChildren().get(csIndex-1).getTIndex();
                }
                int t2 = dstCSNode.getTIndex();
                int t3 = srcParentCSNode.getChildren().get(csIndex).getTIndex();
                if(t1<t2 && t2 < t3){
                    treeIndex = t2;
                }else{
                    treeIndex = t3;
                }
//                treeIndex = srcParentCSNode.getChildren().get(csIndex).getTIndex();
            }
        }

        CSNode srcCSNode=null;
        switch (dstCSNode.getCsType()){
            case IF_STMT:
                IfStmt newIf;
                newIf= Refactor.addNewIf(srcParentCSNode.getTreeNode(), treeIndex);
                srcCSNode = new CSNode(newIf, srcParentCSNode.getHeight()+1, CSNode.CSType.IF_STMT, srcParentCSNode, treeIndex);
                break;
            case FOR_STMT:
                ForStmt newFor = Refactor.addNewFor(srcParentCSNode.getTreeNode(), treeIndex);
                srcCSNode = new CSNode(newFor.getBody(), srcParentCSNode.getHeight()+1, CSNode.CSType.FOR_STMT, srcParentCSNode, treeIndex);
                break;
            case FOREACH_STMT:
                ForEachStmt newForEach = Refactor.addNewForEach(srcParentCSNode.getTreeNode(), treeIndex);
                srcCSNode = new CSNode(newForEach.getBody(), srcParentCSNode.getHeight()+1, CSNode.CSType.FOREACH_STMT, srcParentCSNode, treeIndex);
                break;
            case WHILE_STMT:
                WhileStmt newWhile = Refactor.addNewWhile(srcParentCSNode.getTreeNode(), treeIndex);
                srcCSNode = new CSNode(newWhile.getBody(), srcParentCSNode.getHeight()+1, CSNode.CSType.WHILE_STMT, srcParentCSNode, treeIndex);
                break;

        }
        if(srcCSNode!=null){
            srcCSNode.setNew(true);
            srcParentCSNode.getChildren().add(csIndex, srcCSNode);
            for (int i = csIndex+1; i < srcParentCSNode.getChildren().size(); i++) {
                srcParentCSNode.getChildren().get(i).incTIndex(1);
            }
            srcToDst.put(srcCSNode,dstCSNode);
            dstToSrc.put(dstCSNode,srcCSNode);
        }
        this.treeIndex = treeIndex;
        this.csIndex = csIndex;
        this.newCSNode = srcCSNode;
    }

    public CSNode getNewCSNode() {
        return newCSNode;
    }
}
