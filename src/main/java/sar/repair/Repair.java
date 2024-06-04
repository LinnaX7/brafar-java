package sar.repair;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import program.MethodBuilder;
import program.block.BlockNode;
import sar.TestResult;
import sar.repair.utils.Common;
import sar.repair.utils.Utils;
import variables.*;

import java.util.*;

public class Repair {
    Common common;
    Match match;

    TestResult.JavaException javaException;
    public Repair(MethodBuilder buggyM, MethodBuilder correctM, VariableMatch variableMatch, TestResult.JavaException javaException) {
        common=new Common(buggyM,correctM,variableMatch);
        this.javaException = javaException;
    }
    public void match(){
        match=new Match(common);
        match.match();
    }
    public void replace(){
        BlockNode buggyBlock=common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
        for(Pair<Node,Node> pair: match.getReplaceList()){
            Replace replace=new Replace(common);
            replace.replaceNode(buggyBlock.getParentNode(),pair.first,pair.second);
        }
    }
    public void insert(){
        BlockNode buggyBlock=common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
        for(Node node: match.getInsertList()){
            Node clone=node.clone();
            Insert insert = new Insert(common);
            insert.insert(buggyBlock.getParentNode(),clone,node);
        }
    }
    public void delete() {
        BlockNode buggyBlock = common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
        ArrayList<Node> defList = new ArrayList<>();
        for(Node node: match.getDeleteList()) {
            if (!Utils.isDef(node)) {
                Delete delete=new Delete(common);
                delete.deleteNode(buggyBlock.getParentNode(), node);
            }else{
                defList.add(node);
            }
        }
        if(defList.isEmpty())
            return;
        for (int i = defList.size() - 1; i >= 0; --i) {
            Node node = defList.get(i);
            List<VariableDeclarator> def = Utils.getVariableDecl(node);
            for (int j = def.size() - 1; j >= 0; j--) {
                VariableDeclarator v = def.get(j);
                GraphAdjacencyMatrix matrix;
                if (buggyBlock.getParentBlock() == null) {
                    matrix = VariableDependency.getGraphAdjacencyMatrixFromMethodD(common.buggyMethodBuilder.getMethodDeclaration(), common.buggyVariableBuilder);
                } else
                    matrix = VariableDependency.getGraphAdjacencyMatrixFromBlock(buggyBlock.getParentBlock(), common.buggyVariableBuilder);
                if (v.getInitializer().isPresent()) {
                    Variable b = common.buggyVariableBuilder.getVariable(v.getName().getIdentifier(),
                            common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek()).getParentNode());
                    if (matrix.getBeRelied(common.buggyVariableBuilder.getIndex(b)).size() == 0) {
                        if (def.size() == 1) {
                            Delete delete = new Delete(common);
                            delete.deleteNode(buggyBlock.getParentNode(), node);
                        } else {
                            if (v.getParentNode().isPresent()) {
                                v.getParentNode().get().remove(v);
                            }
                        }
                    }else if(javaException!=null){
                        boolean flag = false;
                        if(node.getBegin().isPresent()) {
                            if(javaException.getLocation()==node.getBegin().get().line){
                                flag = true;
                            }else if(javaException.getLocation()>node.getBegin().get().line){
                                if(node.getEnd().isPresent()){
                                    if(javaException.getLocation()<=node.getEnd().get().line)
                                        flag = true;
                                }
                            }
                        }
                        if(flag){
                            if(b.getType().isPrimitiveType()) {
                                if (b.getType().asPrimitiveType().getType().ordinal() == 0) {
                                    v.setInitializer("false");
                                } else {
                                    v.setInitializer("0");
                                }
                            }else {
                                v.setInitializer("null");
                            }
                        }
                    }
                }
            }
        }
    }
    public void repair(int index){
        common.blockIndex.push(index);

        match();


        insert();
        replace();
        delete();

    }
    public void repair(BlockNode blockNode, ArrayList<Integer> fixed){
        if(blockNode==null){
            return;
        }
        if(blockNode.getMetaIndex()!=-1){
            repair(blockNode.getMetaIndex());
            fixed.add(blockNode.getMetaIndex());
        }else{
            for(BlockNode child:blockNode.getChildBlocks()){
                repair(child,fixed);
            }
        }
    }
    public void execRepair(BlockNode blockNode,ArrayList<Integer> fixed){
        Utils.initDefinition(common);

        repair(blockNode,fixed);

        Reorder reorder=new Reorder(common);
        reorder.reorder();

        Utils.castReturnExp(common);

        Utils.initDefinition(common);
    }
    public Common getCommon() {
        return common;
    }
}
