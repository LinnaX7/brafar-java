package cfs;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;


public class RefactorPro {
    //check if parent node is blockStmt
    public static void CheckParent(Node parent,int index){
        if(parent instanceof BlockStmt){
            NodeList<Statement>parentList=((BlockStmt) parent).getStatements();
            for(int i=index+1;i<parentList.size();i++){
                Statement stmt=parentList.get(i);
                parentList.remove(stmt);
                parentList.add(i,stmt);
            }
        }
    }
    //BB -> if(true){BB}
    public static void rule1(Node n) {
        Node parent=n.getParentNode().get();
        BlockStmt blockStmt=new BlockStmt();
        blockStmt.addStatement((Statement) n);
        IfStmt ifStmt=new IfStmt(new BooleanLiteralExpr(true),blockStmt,null);
        parent.replace(n,ifStmt);
        int index=parent.getChildNodes().indexOf(n);
        CheckParent(parent,index);
    }
    //BB -> if(false){}else{BB}
    public static void rule2(Node n){
        Node parent=n.getParentNode().get();
        BlockStmt thenStmt=new BlockStmt();
        thenStmt.addStatement(new EmptyStmt());
        BlockStmt elseStmt=new BlockStmt();
        elseStmt.addStatement((Statement) n);
        IfStmt ifStmt=new IfStmt(new BooleanLiteralExpr(false),thenStmt,elseStmt);
        parent.replace(n,ifStmt);
        int index=parent.getChildNodes().indexOf(n);
        CheckParent(parent,index);
    }
    //BB -> while(true){BB;break;}
    public static void rule3(Node n){
        Node parent=n.getParentNode().get();
        BlockStmt blockStmt=new BlockStmt();
        blockStmt.addStatement((Statement) n);
        blockStmt.addStatement(new BreakStmt(" "));
        WhileStmt whileStmt=new WhileStmt(new BooleanLiteralExpr(true),blockStmt);
        parent.replace(n,whileStmt);
        int index=parent.getChildNodes().indexOf(n);
        CheckParent(parent,index);
    }
    //BB -> for(;true;){BB;break;}
    public static void rule4(Node n){
        Node parent=n.getParentNode().get();
        BlockStmt blockStmt=new BlockStmt();
        blockStmt.addStatement((Statement) n);
        blockStmt.addStatement(new BreakStmt(" "));
        NodeList<Expression> nullExpr=new NodeList<>();
        nullExpr.add(new NullLiteralExpr());
        ForStmt forStmt=new ForStmt(nullExpr,new BooleanLiteralExpr(true),nullExpr,blockStmt);
        parent.replace(n,forStmt);
        int index=parent.getChildNodes().indexOf(n);
        CheckParent(parent,index);
    }
}
