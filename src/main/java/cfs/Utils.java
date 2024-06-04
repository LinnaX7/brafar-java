package cfs;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.LinkedList;
import java.util.List;

public class Utils {
    public static int getChildIndex(Node n){
        if(n.getParentNode().isEmpty())
            return 0;
        Node parent=n.getParentNode().get();
        int index=0;
        for(Statement node:((BlockStmt)parent).getStatements()){
            if(node.equals(n)){
                return index;
            }
            index++;
        }
        return 0;
    }
    public static boolean insertNode(Node n, List<Node> list, int index){
        if(list.size()<index){
            return false;
        }
        List<Node>newList=new LinkedList<>();
        for(int i=0;i<list.size();i++){
            if(i==index){
                newList.add(n);
            }
            newList.add(list.get(i));
        }
        list.clear();
        list.addAll(newList);
        return true;
    }

}
