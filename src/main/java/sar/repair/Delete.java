package sar.repair;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import program.block.BlockNode;
import sar.repair.utils.Common;
import sar.repair.utils.Utils;
import variables.Variable;
import variables.VariableBuilder;
import variables.VariableMatch;

import java.util.List;

public class Delete {
    Common common;
    BlockNode buggyBlock;
    public Delete(Common common) {
        this.common=common;
        this.buggyBlock=common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
    }
    //delete node
    public void deleteNode(Node parent, Node node){
        if(Utils.isDef(node)){
            List<VariableDeclarator> vars=Utils.getVariableDecl(node);
            for(VariableDeclarator var:vars){
                VariableMatch.removePreMatch(var.getName().getIdentifier(), common.buggyVariableBuilder.getScopeIndex(
                        Variable.getScope(var)));
            }
        }
        if(parent instanceof BlockStmt && node instanceof Statement){
            BlockStmt parentBlock=(BlockStmt) parent;
            int index= Utils.getIndex((BlockStmt) parent,(Statement) node);
            assert(index!=-1);
            parentBlock.getStatements().remove(index);
        }else{
            parent.remove(node);
        }
        int index=Utils.getIndex(buggyBlock.getTreeNodes(),node);
        if(index!=-1)
            buggyBlock.getTreeNodes().remove(index);
        if(buggyBlock.getJumpBlock()!=null){
            if(buggyBlock.getJumpBlock().getTreeNode()==node){
                buggyBlock.setJumpBlock(null);
            }
        }
    }
}
