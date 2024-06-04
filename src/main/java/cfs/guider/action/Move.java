package cfs.guider.action;

import cfs.Refactor;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import cfs.guider.CSNode;
import com.github.javaparser.ast.stmt.Statement;

import java.util.ArrayList;
import java.util.HashMap;

public class Move extends Action{
    ArrayList<CSNode> removeCSNodes;
    NodeList<Statement> removeNodes;

//    public void addHeight(CSNode csNode){
//        csNode.setHeight(csNode.getHeight()+1);
//        for(CSNode node:csNode.getChildren()){
//            addHeight(node);
//        }
//    }

    public void changeNewIfCondition(CSNode csNode){
        if(csNode.getCsType() == CSNode.CSType.ELSE_BRANCH && csNode.getParent().isNew()){
            Refactor.conditionChange((IfStmt) csNode.getParent().getTreeNode());
            csNode.getParent().setNew(true);
        }
    }
    public void changeNewElseAncCondition(CSNode csNode){
        changeNewIfCondition(csNode);
        csNode = csNode.getParent();
        for(CSNode anc:csNode.getAncestors()){
            changeNewIfCondition(anc);
        }
    }



    private ArrayList<CSNode> getChildrenToBeMoved(CSNode dstCSNode, HashMap<CSNode,CSNode> dstToSrc){
        ArrayList<CSNode> childrenToBeMoved = new ArrayList<>();
        for(CSNode child:dstCSNode.getChildren()){
            CSNode sChild = dstToSrc.get(child);
            if(sChild !=null){
                childrenToBeMoved.add(sChild);
            }
        }
        return childrenToBeMoved;
    }

    private void moveCSNodes(ArrayList<CSNode> csNodes, CSNode branchNode){
        changeNewElseAncCondition(branchNode);
        CSNode beginChildCSNode = csNodes.get(0);
        CSNode endChildCSNode = csNodes.get(csNodes.size()-1);
        CSNode childP = beginChildCSNode.getParent();
        while(endChildCSNode.getParent() != childP)
            endChildCSNode = endChildCSNode.getParent();
        removeCSNodesFromCSNode(childP, childP.getChildren().indexOf(beginChildCSNode), childP.getChildren().indexOf(endChildCSNode));
        branchNode.setChildren(this.removeCSNodes);
        for(CSNode reCSNode: removeCSNodes){
            reCSNode.setParent(branchNode);
            reCSNode.setHeight(branchNode.getHeight()+1);
            reCSNode.updateChildrenHeight();
        }
        if(removeNodes.size()!=0){
            ((BlockStmt)branchNode.getTreeNode()).setStatements(removeNodes);
        }
    }

    private void removeCSNodesFromCSNode(CSNode csNode, int begin, int end){
        removeCSNodes = new ArrayList<>();
        removeNodes = new NodeList<>();
        int c= csNode.getChildren().get(end).getTIndex() - csNode.getChildren().get(begin).getTIndex() + 1;

        for (int i = csNode.getChildren().get(begin).getTIndex(); i < csNode.getChildren().get(end).getTIndex()+1; i++) {
            removeNodes.add(((BlockStmt)csNode.getTreeNode()).getStatements().remove(csNode.getChildren().get(begin).getTIndex()));
        }
        if(((BlockStmt)csNode.getTreeNode()).getStatements().size()==0){
            ((BlockStmt)csNode.getTreeNode()).getStatements().add(new EmptyStmt());
        }
        int tBegin = csNode.getChildren().get(begin).getTIndex();
        for (int i = begin; i < end+1; i++) {
            csNode.getChildren().get(begin).incTIndex(-tBegin);
            removeCSNodes.add(csNode.getChildren().remove(begin));
        }
        for (int i = begin; i < csNode.getChildren().size(); i++) {
            csNode.getChildren().get(i).incTIndex(-c);
        }
    }


    public Move(CSNode srcCSNode,CSNode dstCSNode, HashMap<CSNode,CSNode> dstToSrc){
        super(ActionType.MOVE);
        ArrayList<CSNode> childrenToBeMoved = getChildrenToBeMoved(dstCSNode, dstToSrc);
        if(childrenToBeMoved.size()>0){
            moveCSNodes(childrenToBeMoved, srcCSNode);
        }

//
//        int csIndex=dstCSNode.getParent().getChildren().indexOf(dstCSNode);
//        int treeIndex=0;
//
//        if(srcParentCSNode.getChildren().size()!=0){
//            Node treeNode;
//            if(srcParentCSNode.getChildren().size()<=csIndex){
//                treeNode = srcParentCSNode.getChildren().get(srcParentCSNode.getChildren().size() - 1).getTreeNode();
//            }else{
//                treeNode = srcParentCSNode.getChildren().get(csIndex).getTreeNode();
//            }
//            treeIndex=treeNode.getParentNode().get().getChildNodes().indexOf(treeNode);
//        }
//
//        Node srcParentTreeNode=srcParentCSNode.getTreeNode();
//        if(srcCSNode.getCsType()== CSNode.CSType.ELSE_STMT){

//        }else{
//            srcCSNode.getTreeNode().getParentNode().get().remove(srcCSNode.getTreeNode());
//            if(srcParentTreeNode instanceof BlockStmt){
//                BlockStmt blockStmt=(BlockStmt) srcParentTreeNode;
//                NodeList<Statement> stmtList=blockStmt.getStatements();
//                stmtList.add(treeIndex,(Statement) srcCSNode.getTreeNode());
//                int i=treeIndex+1;
//                for(int j=i;j<stmtList.size();j++){
//                    Statement stmt=stmtList.get(j);
//                    stmtList.remove(stmt);
//                    stmtList.add(j,stmt);
//                }
//            }
//        }
//
//        srcParentCSNode.getChildren().add(csIndex,srcCSNode);
//        srcCSNode.getParent().getChildren().remove(srcCSNode);
//        srcCSNode.setParent(srcParentCSNode);
//        addHeight(srcCSNode);
    }

}
