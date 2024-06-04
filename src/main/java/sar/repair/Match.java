package sar.repair;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import program.block.BlockNode;
import program.block.BlockType;
import sar.repair.utils.Common;
import sar.repair.utils.Utils;
import variables.Variable;


import java.util.ArrayList;
import java.util.List;

public class Match {
    private final BlockNode buggyBlock;
    private final BlockNode correctBlock;
    Common common;

    List<Pair<Node,Node>> replaceList;   //replace
    List<Node> deleteList;              //delete
    List<Node> insertList;              //add

    public Match(Common common){
        buggyBlock=common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
        correctBlock=common.correctMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
        this.common=common;

        replaceList=new ArrayList<>();
        deleteList=new ArrayList<>();
        insertList=new ArrayList<>();
    }
    //type of whole expression
    public boolean Cond1(Node buggyNode,Node correctNode){
        return buggyNode.getClass().getName().equals(correctNode.getClass().getName());
    }
    //type of every sub expressions
    public boolean Cond2(Node buggyNode,Node correctNode){
        boolean flag=buggyNode.getChildNodes().size()==correctNode.getChildNodes().size();
        if(flag){
            for(int i=0;i<buggyNode.getChildNodes().size(); i++){
                flag=flag&&Cond1(buggyNode.getChildNodes().get(i),correctNode.getChildNodes().get(i));
            }
        }
        return flag;
    }
    //every variable is matched in order
    public boolean Cond3(Node buggyNode,Node correctNode){
        List<Variable>buggyVariables=new ArrayList<>();
        common.buggyVariableBuilder.getVariablesFromNode(buggyNode,buggyVariables);
        List<Variable>correctVariables=new ArrayList<>();
        common.correctVariableBuilder.getVariablesFromNode(correctNode,correctVariables);
        if(buggyVariables.size()==correctVariables.size()){
            for(int i=0;i<buggyVariables.size();i++){
                if(!common.variableMatch.getB2cMatch().containsKey(buggyVariables.get(i))){
                    return false;
                }
                if(!common.variableMatch.getB2cMatch().get(buggyVariables.get(i)).equals(correctVariables.get(i))){
                    return false;
                }
            }
            return  true;
        }
        return false;
    }
    //every buggy variable is matched
    public boolean Cond4(Node buggyNode, Node correctNode){
        List<Variable>buggyVariables=new ArrayList<>();
        common.buggyVariableBuilder.getVariablesFromNode(buggyNode,buggyVariables);
        List<Variable>correctVariables=new ArrayList<>();
        common.correctVariableBuilder.getVariablesFromNode(correctNode,correctVariables);
        boolean flag=false;
        for(Variable var:buggyVariables){
            if(common.variableMatch.getB2cMatch().containsKey(var)){
                if(correctVariables.contains(common.variableMatch.getB2cMatch().get(var))){
                    flag=true;
                }
            }
            if(!flag){
                return false;
            }
            flag = false;
        }
        return true;
    }
    //every correct variable is matched
    public boolean Cond5(Node buggyNode,Node correctNode){
        List<Variable>buggyVariables=new ArrayList<>();
        common.buggyVariableBuilder.getVariablesFromNode(buggyNode,buggyVariables);
        List<Variable>correctVariables=new ArrayList<>();
        common.correctVariableBuilder.getVariablesFromNode(correctNode,correctVariables);
        boolean flag=false;
        for(Variable var:correctVariables){
            if(common.variableMatch.getC2bMatch().containsKey(var)){
                if(buggyVariables.contains(common.variableMatch.getB2cMatch().get(var))){
                    flag=true;
                }
            }
            if(!flag){
                return false;
            }
            flag = false;
        }
        return true;
    }
    public boolean isSameDecl(Node buggyNode,Node correctNode){
        List<VariableDeclarator>buggy=Utils.getVariableDecl(buggyNode);
        List<VariableDeclarator>correct=Utils.getVariableDecl(correctNode);
        if(buggy.size()==correct.size()){
            List<VariableDeclarator>bRemove=new ArrayList<>();
            List<VariableDeclarator>cRemove=new ArrayList<>();
            for(VariableDeclarator b:buggy){
                for(VariableDeclarator c:correct){
                    Variable var=common.correctVariableBuilder.getVariable(c.getName().toString(),c);
                    if(common.variableMatch.getC2bMatch().containsKey(var)){
                        if(common.variableMatch.getC2bMatch().get(var).equals(common.buggyVariableBuilder.getVariable(b.getName().toString(),b))){
                            if(!(b.getInitializer().isPresent()&& c.getInitializer().isEmpty())){
                                bRemove.add(b);
                                cRemove.add(c);
                            }
                        }
                    }
                }
                correct.removeAll(cRemove);
            }
            buggy.removeAll(bRemove);
            return buggy.isEmpty()&&correct.isEmpty();
        }
        return false;
    }
    public boolean isMatch(Node buggyNode,Node correctNode,int select){
        if(buggyBlock.getBlockType().equals(BlockType.IF_COND)&&correctBlock.getBlockType().equals(BlockType.IF_COND)){
            return true;
        }
        if(buggyBlock.getBlockType().equals(BlockType.WHILE_COND)&&buggyBlock.getBlockType().equals(BlockType.WHILE_COND)){
            return true;
        }
        if(buggyBlock.getBlockType().equals(BlockType.FOR_INIT)&&correctBlock.getBlockType().equals(BlockType.FOR_INIT)){
            return isSameDecl(buggyNode,correctNode);
        }
        if(buggyBlock.getBlockType().equals(BlockType.FOR_COMP)&&correctBlock.getBlockType().equals(BlockType.FOR_COMP)){
            return true;
        }
        if(buggyBlock.getBlockType().equals(BlockType.FOR_UPDATE) && correctBlock.getBlockType().equals(BlockType.FOR_UPDATE)){
            return true;
        }
        if(buggyBlock.getBlockType().equals(BlockType.FOREACH_ITER)&&correctBlock.getBlockType().equals(BlockType.FOREACH_ITER)){
            return true;
        }
        if(!isSameDecl(buggyNode,correctNode)){
            return false;
        }
        if(select==0){
            return Cond1(buggyNode,correctNode)&&Cond2(buggyNode,correctNode)&&Cond3(buggyNode, correctNode);
        }else if(select == 1){
            return Cond1(buggyNode,correctNode)&&Cond2(buggyNode,buggyNode)&&Cond4(buggyNode,correctNode)&&Cond5(buggyNode,correctNode);
        }else{
            return Cond1(buggyNode,correctNode);
        }
    }
    public void showMatch(){
        System.out.println("------------------------Replace-----------------------");
        for (Pair<Node,Node>pair:replaceList){
            System.out.println("{"+pair.first.toString()+"} match {" +pair.second.toString()+"}");
        }
        System.out.println("------------------------Insert-------------------------");
        for(Node node:insertList){
            System.out.println(node.toString());
        }
        System.out.println("------------------------Delete-------------------------");
        for(Node node:deleteList){
            System.out.println(node.toString());
        }
    }
    public void match(){
        System.out.println("--------------------------------------Statement match--------------------------------------");
        replaceList.clear();
        insertList.clear();
        deleteList.clear();
        //match jump node
        if(buggyBlock.getJumpBlock()!=null&&correctBlock.getJumpBlock()!=null){
            Node buggyJump=buggyBlock.getJumpBlock().getTreeNode();
            Node correctJump=correctBlock.getJumpBlock().getTreeNode();
            replaceList.add(new Pair<>(buggyJump,correctJump));
        }else if(buggyBlock.getJumpBlock()!=null){
            Node buggyJump=buggyBlock.getJumpBlock().getTreeNode();
            deleteList.add(buggyJump);
        }else if(correctBlock.getJumpBlock()!=null){
            Node correctJump=correctBlock.getJumpBlock().getTreeNode();
            insertList.add(correctJump);
        }
        //match normal node
        List<Node> buggyNodes=buggyBlock.getTreeNodes();
        List<Node> correctNodes=correctBlock.getTreeNodes();
        List<Node>buggyMatch=new ArrayList<>();
        List<Node>correctMatch=new ArrayList<>();
        int selects=3;
        for(int i=0;i<selects;i++){
            for(Node buggyNode:buggyNodes){
                for(Node correctNode:correctNodes){
                    if(isMatch(buggyNode,correctNode,i)&&(!buggyMatch.contains(buggyNode))
                            &&(!correctMatch.contains(correctNode))){
                        replaceList.add(new Pair<>(buggyNode,correctNode));
                        buggyMatch.add(buggyNode);
                        correctMatch.add(correctNode);
                    }
                }
            }
        }
        for(Node node:buggyNodes){
            if(!buggyMatch.contains(node)){
                deleteList.add(node);
            }
        }
        for(Node node:correctNodes){
            if(!correctMatch.contains(node))
                insertList.add(node);
        }
        showMatch();
    }
    public List<Pair<Node, Node>> getReplaceList() {
        return replaceList;
    }
    public List<Node> getInsertList() {
        return insertList;
    }
    public List<Node> getDeleteList() {
        return deleteList;
    }
}
