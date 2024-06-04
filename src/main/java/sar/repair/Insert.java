package sar.repair;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.Statement;
import program.block.BlockNode;
import program.block.BlockType;
import sar.repair.utils.Location;
import sar.repair.utils.Common;
import sar.repair.utils.Utils;
import variables.Variable;

import java.util.List;
import java.util.Map;


public class Insert {
    Common common;
    public Insert(Common common) {
        this.common=common;
    }
    //insert node
    public void insertNode(Node parent,Node buggy,Node correct){
        int index= Utils.getBlockRange(common.buggyMethodBuilder,common.blockIndex.peek()).second+1;
        BlockNode buggyNode=common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
        if(parent instanceof BlockStmt && buggy instanceof Statement){
            Utils.insertStmt((BlockStmt) parent,(Statement) buggy,index);
        }else if(parent instanceof ForStmt &&buggy instanceof Expression){
            if(buggyNode.getBlockType().equals(BlockType.FOR_INIT)) {
                if(((ForStmt)parent).getInitialization().isNonEmpty()){
                    buggyNode.getTreeNodes().removeAll(((ForStmt)parent).getInitialization());
                }
                ((ForStmt) parent).setInitialization(new NodeList<>((Expression)buggy));
            }else if(buggyNode.getBlockType().equals(BlockType.FOR_COMP)){
                if(((ForStmt)parent).getCompare().isPresent()){
                    buggyNode.getTreeNodes().remove(((ForStmt)parent).getCompare().get());
                }
                ((ForStmt)parent).setCompare((Expression) buggy);
            }else if(buggyNode.getBlockType().equals(BlockType.FOR_UPDATE)){
                if(((ForStmt)parent).getUpdate().isNonEmpty()){
                    buggyNode.getTreeNodes().removeAll(((ForStmt)parent).getUpdate());
                }
                ((ForStmt)parent).setUpdate(new NodeList<>((Expression) buggy));
            }
        }
        buggyNode.getTreeNodes().add(buggy);

        if(parent instanceof BlockStmt){
            if(correct.getParentNode().isPresent()) {
                int correctIndex=Utils.getIndex(correct);
                //int correctIndex = correct.getParentNode().get().getChildNodes().indexOf(correct);
                Pair<Integer, Integer> correctRange = Utils.getBlockRange(common.correctMethodBuilder, common.blockIndex.peek());
                common.order.put(new Location(common.blockIndex.peek(), correctIndex - correctRange.first), buggy);
            }
        }
    }
    public void insert(Node parent,Node buggy,Node correct){
        if(Utils.isDef(buggy)){
            List<VariableDeclarator>varDecl=Utils.getVariableDecl(buggy);
            if(varDecl.size()==1){
                VariableDeclarator var = varDecl.get(0);
                Variable c=common.correctVariableBuilder.getVariable(var.getName().toString(),correct);
                Variable b=common.buggyVariableBuilder.getVariable(var.getName().toString(),
                        common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek()).getParentNode());
                if(common.variableMatch.getC2bMatch().containsKey(c)){
                    VariableDeclarator decl=common.buggyVariableBuilder.getVariableDeclarator(
                                common.variableMatch.getC2bMatch().get(c));
                    int declBlockIndex= Utils.getDeclarationBlock(common.buggyVariableBuilder,decl);
                    if(var.getInitializer().isPresent()||(
                            var.getInitializer().isEmpty() &&declBlockIndex>common.blockIndex.peek())) {
                        if(var.getInitializer().isEmpty()){
                            if(var.getType().isPrimitiveType()){
                                if(var.getType().asPrimitiveType().getType().ordinal()==0){
                                    var.setInitializer("false");
                                }else{
                                    var.setInitializer("0");
                                }
                            }else{
                                var.setInitializer("null");
                            }
                        }
                        if(decl.getType().isPrimitiveType()){
                            if(decl.getType().asPrimitiveType().getType().toBoxedType().getName().toString().equals("Float")){
                                if(var.getInitializer().get().isDoubleLiteralExpr()){
                                    String value=var.getInitializer().get().toString();
                                    if(!(value.contains("f")||value.contains("F"))){
                                        var.setInitializer(value+"f");
                                    }
                                }
                            }
                        }

                        AssignExpr assignExpr=new AssignExpr(new NameExpr(var.getName()),var.getInitializer().get(), AssignExpr.Operator.ASSIGN);
                        Node buggyNode=assignExpr;
                        if(parent instanceof BlockStmt) {
                            buggyNode = new ExpressionStmt(assignExpr);
                        }
                        insertNode(parent,buggyNode,correct);
//                        if(declBlockIndex>common.blockIndex.peek()){
//
//                        }
                        Utils.changeVar(buggyNode,correct,common);


                        if(parent instanceof BlockStmt){
                            if(declBlockIndex==common.blockIndex.peek()){
                                for(Map.Entry<Location,Node>entry:common.order.entrySet()){
                                    if(entry.getValue()==buggyNode){
                                        common.order.remove(entry.getKey());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }else {
                    String correctName=var.getName().toString();
                    if(b!=null){
                        var.setName(new SimpleName(var.getName()+"__"+Utils.randomIdentifier()));
                    }
                    VariableDeclarationExpr varExpr = new VariableDeclarationExpr(var);
                    if(parent instanceof  BlockStmt){
                        ExpressionStmt exp=new ExpressionStmt(varExpr);
                        insertNode(parent,exp,correct);

                        common.buggyVariableBuilder.visit(var,null);
                        Utils.addMatch(var.getName().toString(),var,correctName,correct,common);
                        Utils.changeType(exp,correct,common);
                        Utils.changeVar(exp,correct,common);
                    }
                }
            }else{
                for(VariableDeclarator var:varDecl){
                    insert(parent,var,correct);
                }
            }
        }else{
            insertNode(parent,buggy,correct);
            Utils.changeType(buggy,correct,common);
            Utils.changeVar(buggy,correct,common);
        }
    }
}
