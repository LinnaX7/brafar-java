package cfs;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.*;

public class Format {

    public static void formatFor(ForStmt forStmt){
        if(!(forStmt.getBody() instanceof BlockStmt)){
            BlockStmt blockStmt=new BlockStmt();
            Node body = forStmt.getBody();
            blockStmt.addStatement(forStmt.getBody());
            forStmt.setBody(blockStmt);
            body.setParentNode(blockStmt);
            if(blockStmt.getStatements().getFirst().isEmpty()){
                blockStmt.addStatement(new EmptyStmt());
            }
        }
    }

    public static void formatForEach(ForEachStmt node){
        if(!(node.getBody() instanceof BlockStmt)){
            BlockStmt blockStmt=new BlockStmt();
            Node body = node.getBody();
            blockStmt.addStatement(node.getBody());
            node.setBody(blockStmt);
            body.setParentNode(blockStmt);
            if(blockStmt.getStatements().getFirst().isEmpty()){
                blockStmt.addStatement(new EmptyStmt());
            }
        }
    }

    public static void formatWhile(WhileStmt node){
        if(!(node.getBody() instanceof BlockStmt)){
            BlockStmt blockStmt=new BlockStmt();
            Node body = node.getBody();
            blockStmt.addStatement(node.getBody());
            node.setBody(blockStmt);
            body.setParentNode(blockStmt);
            if(blockStmt.getStatements().getFirst().isEmpty()){
                blockStmt.addStatement(new EmptyStmt());
            }
        }
    }

    public static void formatIf(IfStmt node){
        if(!(node.getThenStmt() instanceof BlockStmt)){
            BlockStmt blockStmt=new BlockStmt();
            Node body = node.getThenStmt();
            blockStmt.addStatement(node.getThenStmt());
            node.setThenStmt(blockStmt);
            body.setParentNode(blockStmt);
            if(blockStmt.getStatements().getFirst().isEmpty()){
                blockStmt.addStatement(new EmptyStmt());
            }
        }
        if(node.getElseStmt().isPresent()){
            if(!(node.getElseStmt().get() instanceof BlockStmt)){
                BlockStmt blockStmt=new BlockStmt();
                Node body = node.getElseStmt().get();
                blockStmt.addStatement(node.getElseStmt().get());
                node.setElseStmt(blockStmt);
                body.setParentNode(blockStmt);
                if(blockStmt.getStatements().getFirst().isEmpty()){
                    blockStmt.addStatement(new EmptyStmt());
                }
            }
        }
    }

    public static void formatCompositeNode(Node n){
        n.findAll(IfStmt.class).forEach(Format::formatIf);
        n.findAll(ForStmt.class).forEach(Format::formatFor);
        n.findAll(ForEachStmt.class).forEach(Format::formatForEach);
        n.findAll(WhileStmt.class).forEach(Format::formatWhile);
    }

    public static void formatProgram(Node n){
        formatCompositeNode(n);
        Refactor.rule1(n);
    }
}
