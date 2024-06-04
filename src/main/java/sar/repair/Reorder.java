package sar.repair;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import program.block.BlockNode;
import sar.repair.utils.Common;
import sar.repair.utils.Utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Reorder {
    Common common;
    public Reorder(Common common){
        this.common=common;
    }
    public void reorder(){
        System.out.println("-------------------------------Reorder---------------------------------------------------");
        Set<Integer> isOrder=new HashSet<>();
        common.order.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(map->{
                            BlockNode blockNode=common.buggyMethodBuilder.getMetaBlockNodes().get(map.getKey().block);
                            Node parent=blockNode.getParentNode();
                            Node node=map.getValue();
                            Pair<Integer,Integer> range= Utils.getBlockRange(common.buggyMethodBuilder,map.getKey().block);
                            int index=range.first;
                            Utils.getBlockRange(common.buggyMethodBuilder,map.getKey().block);

                            if(map.getKey().sequence!=-1){
                                if(!Utils.isDef(node)){
                                    index+=map.getKey().sequence;
                                }
                            }
                            while (isOrder.contains(index)){
                                index++;
                            }
                            isOrder.add(index);
                            if(parent instanceof BlockStmt && node instanceof Statement){
                                ((BlockStmt)parent).getStatements().remove(node);
                                Utils.insertStmt((BlockStmt) parent,(Statement) node,index);
                            }
                            blockNode.getTreeNodes().remove(node);
                            blockNode.getTreeNodes().add(node);
                            System.out.println(map.getKey().toString()+ map.getValue().toString());
                        }
                );
        for(BlockNode block : common.buggyMethodBuilder.getMetaBlockNodes()){
            Node parent=block.getParentNode();
            if(parent instanceof BlockStmt && block.getJumpBlock()!=null){
                Node node=block.getJumpBlock().getTreeNode();
                if(node instanceof Statement){
                    int index=parent.getChildNodes().size()-1;
                    if(((BlockStmt)parent).getStatements().remove(node))
                        Utils.insertStmt((BlockStmt) parent,(Statement) node,index);
                }
            }
        }
    }
}
