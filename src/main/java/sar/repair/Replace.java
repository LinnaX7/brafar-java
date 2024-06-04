package sar.repair;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.stmt.*;
import program.block.BlockNode;
import sar.repair.utils.Location;
import sar.repair.utils.Common;
import sar.repair.utils.Utils;
import variables.Variable;

import java.util.List;

public class Replace {
    Common common;
    BlockNode buggyBlock;
    public Replace(Common common) {
        this.common=common;
        buggyBlock=common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
    }
    //replace node
    public void replaceNode(Node parent, Node node, Node replacementNode){
        int buggyIndex=Utils.getIndex(node);
        //int buggyIndex=parent.getChildNodes().indexOf(node);

        Pair<Integer,Integer> buggyRange= Utils.getBlockRange(common.buggyMethodBuilder,common.blockIndex.peek());
        Node clone=replacementNode.clone();
        if(node instanceof ReturnStmt){
            common.buggyVariableBuilder.getReturnStmts().remove(node);
            if(!(clone instanceof EmptyStmt) && clone instanceof ReturnStmt)
                common.buggyVariableBuilder.getReturnStmts().add((ReturnStmt) clone);
            else {
                if(parent instanceof BlockStmt){
                    NodeList<Statement> parentList=((BlockStmt)parent).getStatements();
                    parent.replace(node,clone);
                    int size=parentList.size();
                    for(int i=buggyIndex+1;i<size;i++){
                        Statement stmt=parentList.get(i);
                        //parentList.set(i,stmt);
                        parentList.remove(i);
                        parentList.add(i,stmt);
                    }
                }
                return;
            }
        }
        if(!Utils.isDef(node)&&Utils.isDef(clone)){
            List<VariableDeclarator>decls=Utils.getVariableDecl(clone);
            if(clone instanceof ExpressionStmt){
                assert decls.get(0).getInitializer().isPresent();
                ((ExpressionStmt)clone).setExpression(new AssignExpr(decls.get(0).getNameAsExpression(),
                        decls.get(0).getInitializer().get(), AssignExpr.Operator.ASSIGN));
            }
        }
        parent.replace(node,clone);
        if(parent instanceof BlockStmt){
            NodeList<Statement> parentList=((BlockStmt)parent).getStatements();
            int size=parentList.size();
            for(int i=buggyIndex+1;i<size;i++){
                Statement stmt=parentList.get(i);
                //parentList.set(i,stmt);
                parentList.remove(i);
                parentList.add(i,stmt);
            }
        }

        if(clone instanceof ReturnStmt ||clone instanceof BreakStmt ||clone instanceof ContinueStmt){
            buggyBlock.getJumpBlock().setTreeNode(clone);
            if(node instanceof ReturnStmt) {
                if(((ReturnStmt) node).getExpression().isPresent()) {
                    if(((ReturnStmt)clone).getExpression().isPresent()){
                        Variable l = common.buggyVariableBuilder.getReturnVariable(((ReturnStmt) node).getExpression().get());
                        common.buggyVariableBuilder.putReturnExpression(l, ((ReturnStmt)clone).getExpression().get());
                    }
                }
            }
        }else{
            if(parent instanceof BlockStmt){
                buggyBlock.getTreeNodes().remove(node);
                int relativeIndex=buggyIndex-buggyRange.first;
                if(relativeIndex>buggyBlock.getTreeNodes().size()){
                    relativeIndex=buggyBlock.getTreeNodes().size() ;
                }
                if(relativeIndex<0){
                    relativeIndex=0;
                }
                buggyBlock.getTreeNodes().add(relativeIndex,clone);
            }else{
                buggyBlock.getTreeNodes().remove(node);
                buggyBlock.getTreeNodes().add(clone);
            }
        }
        if(Utils.isDef(clone)){
            List<VariableDeclarator> varList=Utils.getVariableDecl(clone);
            for(VariableDeclarator varDecl : varList){
                String name=varDecl.getName().getIdentifier();
                Variable c=common.correctVariableBuilder.getVariable(name,replacementNode);
                if(c==null&&name.length()>3){
                    c=common.correctVariableBuilder.getVariable(name.substring(3), replacementNode);
                }
                if(c!=null) {
                    if (common.variableMatch.getC2bMatch().containsKey(c)) {
                        Variable b=common.variableMatch.getC2bMatch().get(c);
                        common.buggyVariableBuilder.V2V.remove(b);
                        common.buggyVariableBuilder.V2V.put(b,varDecl);
                    }
                }
            }
        }

        Utils.changeType(clone,replacementNode,common);
        Utils.changeVar(clone,replacementNode,common);

        if(parent instanceof BlockStmt){
            if(replacementNode.getParentNode().isPresent()) {
                int correctIndex=Utils.getIndex(replacementNode);
                ///int correctIndex = replacementNode.getParentNode().get().getChildNodes().indexOf(replacementNode);
                Pair<Integer, Integer> correctRange = Utils.getBlockRange(common.correctMethodBuilder, common.blockIndex.peek());
                common.order.put(new Location(common.blockIndex.peek(), correctIndex - correctRange.first), clone);
            }
        }
    }
}
