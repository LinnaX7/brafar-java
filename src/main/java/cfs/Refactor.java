package cfs;

import cfs.guider.CSNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.printer.YamlPrinter;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Refactor {


    //'''(P If(C1){B1 R1}  S) <-- (P If(C1){B1 R1} Else{S} )'''
    public static boolean containReturn(IfStmt ifStmt){
        for(Statement statement:ifStmt.getThenStmt().asBlockStmt().getStatements()){
            if(statement instanceof ReturnStmt)
                return true;
        }
        return false;
    }
    public static void rule1(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt->{
            if(ifStmt.getElseStmt().isPresent()){
                Statement elseStatement=ifStmt.getElseStmt().get();
                if(containReturn(ifStmt)&&!(elseStatement instanceof IfStmt)) {
                    ifStmt.setElseStmt(null);
                    assert ifStmt.getParentNode().isPresent();
                    Node parent = ifStmt.getParentNode().get();
                    if (parent instanceof BlockStmt) {
                        int index = Utils.getChildIndex(ifStmt);
                        NodeList<Statement> parentList = ((BlockStmt) parent).getStatements();
                        if (elseStatement instanceof BlockStmt) {
                            NodeList<Statement> childList = ((BlockStmt) elseStatement).getStatements();
                            for (Statement statement : childList) {
                                index+=1;
                                //statement.setParentNode(parent);
                                parentList.add(index, statement);
//                              parent.getChildNodes().add(index+1,statement);
                            }

                        } else {
                            index+=1;
                            //elseStatement.setParentNode(parent);
                            parentList.add(index, elseStatement);
                        }
                        index = index+1;
                        for (int i = index; i < parentList.size(); i++) {
                            Statement stmt=parentList.get(i);
                            parentList.remove(stmt);
                            parentList.add(i,stmt);
//                          Node t = parentList.get(i);
//                          parentList.remove(parentList.get(i));
//                          t.setParentNode(parent);
                        }
                    }
                }
            }
        });
    }

    public static void rule122(Node n){
        //'''(P If(C1){B1 R1}  S) --> (P If(C1){B1 R1} Else{S} )'''
        n.findAll(IfStmt.class).forEach(ifStmt-> {
            VoidVisitor voidVisitor = new VoidVisitor();
            voidVisitor.visit(ifStmt.getThenStmt(), null);
            if (voidVisitor.getStrCFS().contains("Return") && ifStmt.getElseStmt().isEmpty()) {
                NodeList<Statement> newList = new NodeList<>();
                assert ifStmt.getParentNode().isPresent();
                Node parent = ifStmt.getParentNode().get();

                List<Node> list = parent.getChildNodes();
                int index = Utils.getChildIndex(ifStmt);
                int i = 0;
                for (Object child : list.toArray()) {
                    if (i > index) {
                        if (child instanceof Statement)
                            newList.add((Statement) child);
                        parent.remove((Node) child);
                    }
                    i++;
                }
                ifStmt.setElseStmt(new BlockStmt(newList));
            }
        });
    }
    //'''(P If(C1){B1 R1} If(C2){B2} S) <--> (P If(C1){B1 R1} Elif(C2){B2} S)'''
    public static void rule2(Node n) {
        n.findAll(IfStmt.class).forEach(ifStmt->{
            VoidVisitor voidVisitor=new VoidVisitor();
            voidVisitor.visit(ifStmt.getThenStmt(),null);
            if(ifStmt.getElseStmt().isPresent()){
                Statement elseStatement=ifStmt.getElseStmt().get();
                assert ifStmt.getParentNode().isPresent();
                Node parent=ifStmt.getParentNode().get();
                if(voidVisitor.getStrCFS().contains("Return")&&elseStatement instanceof IfStmt) {
                    IfStmt elseState=(IfStmt)elseStatement;
                    ifStmt.setElseStmt(null);
                    int index=Utils.getChildIndex(ifStmt);
                    if(parent instanceof BlockStmt){
                        elseState.setParentNode(parent);
                        NodeList<Statement> newList=((BlockStmt) parent).getStatements();
                        newList.add(index+1,elseState);
                    }
                }
            }else{
                Node parent=ifStmt.getParentNode().get();
                int index=Utils.getChildIndex(ifStmt);
                if(index+1!=parent.getChildNodes().size()) {
                    Node next = parent.getChildNodes().get(index + 1);
                    if (voidVisitor.getStrCFS().contains("Return") && next instanceof IfStmt) {
                        ifStmt.setElseStmt((IfStmt) next);
                        parent.remove(next);
                        next.setParentNode(ifStmt);
                    }
                }
            }
        });
    }
    //P If(C1 and C2){B1}B2 S -> P If(C1){If(C2){B1}}B2 S
    //P If(C1 and C2){B1}B2 S -> P If(True){If(C1 and C2){B1}}B2 S
    //P If(C1 and C2){B1}B2 S -> P If(C1 and C2){If(True){B1}}B2 S
    public static void rule3(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt -> {
            Expression cond=ifStmt.getCondition();
            if(cond.isBinaryExpr()){
                BinaryExpr binaryExpr=(BinaryExpr) cond;
                if(binaryExpr.getOperator()== BinaryExpr.Operator.AND){
                    //1
//                    ifStmt.setCondition(binaryExpr.getLeft());
//                    IfStmt innerIfStmt=new IfStmt(binaryExpr.getRight(),ifStmt.getThenStmt(),null);
//                    innerIfStmt.setParentNode(ifStmt);
//                    ifStmt.setThenStmt(innerIfStmt);
                    //2
//                    ifStmt.setThenStmt(ifStmt.clone());
//                    ifStmt.setCondition(new BooleanLiteralExpr(true));
                    //3
                    IfStmt innerIfStmt=new IfStmt(new BooleanLiteralExpr(true),ifStmt.getThenStmt(),null);
                    ifStmt.setThenStmt(innerIfStmt);
                }
            }
        });
    }
    //P S -> P If(*){Pass} S
    //P If(C1){B1}B2 S-> P If(C1){B1}Else{Pass}B2 S
    //P Elif(C1){B1}B2 S-> P Elif(C1){B1}Else{Pass}B2 S
    //P If(C1){B1}B2 S-> P If(C1){B1}Elif(*){Pass}B2 S
    //Pass-->EmptyStmt
    //*-->true/false
    public static void rule4(Node n){
        IfStmt newIF=new IfStmt();
        newIF.setCondition(new BooleanLiteralExpr(true));
        newIF.setThenStmt(new BlockStmt());
        int index=Utils.getChildIndex(n);
        assert n.getParentNode().isPresent();
        Node parent=n.getParentNode().get();
        if(parent instanceof BlockStmt){
            Utils.insertNode(n,parent.getChildNodes(),index);
        }

        n.findAll(IfStmt.class).forEach(ifStmt -> {
            Statement statement=new EmptyStmt();
            NodeList<Statement> statements=new NodeList<>();
            statements.add(statement);


            //3
            if(ifStmt.getElseStmt().isPresent()){
                Statement elseStmt=ifStmt.getElseStmt().get();
                if(elseStmt.isIfStmt()){
                    IfStmt elseIFStmt=(IfStmt) elseStmt;
                    elseIFStmt.setElseStmt(new BlockStmt(statements));
                }
            }else{
                //2
                //ifStmt.setElseStmt(new BlockStmt(statements));

                //4
                ifStmt.setElseStmt(new IfStmt(new BooleanLiteralExpr(true),new BlockStmt(statements),null));
            }

        });
    }

    // P B S -> P If(False){*}B S
    // P If(C1){B1}B2 S-> P If(C1){B1}Elif(False){*}B2 S
    public static void rule5(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt -> {
            Statement statement=new EmptyStmt();
            NodeList<Statement> statements=new NodeList<>();
            statements.add(statement);
            if(!ifStmt.hasElseBranch()){
                ifStmt.setElseStmt(new IfStmt(new BooleanLiteralExpr(false),new BlockStmt(statements),null));
            }
        });
    }
    //P If(C1){If(C2){B1}}B2 S -> P If(C1 and C2){B1}B2 S
    public static void rule6(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt -> {
            if(ifStmt.getThenStmt().isBlockStmt()){
                BlockStmt thenStmt=(BlockStmt) ifStmt.getThenStmt();
                if(thenStmt.getStatements().size()==1){
                    Statement statement=thenStmt.getStatement(0);
                    if(statement.isIfStmt()){
                        IfStmt thenIfStmt=(IfStmt) statement;
                        BinaryExpr binaryExpr=new BinaryExpr(ifStmt.getCondition(),thenIfStmt.getCondition(), BinaryExpr.Operator.AND);
                        ifStmt.setCondition(binaryExpr);
                        ifStmt.setThenStmt(thenIfStmt.getThenStmt());
                    }
                }
            }
        });
    }
    //P Elif(C1){...} S -> P Else{If(C1){...}} S
    public static void rule7(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt -> {
            if(ifStmt.getElseStmt().isPresent()){
                if(ifStmt.getElseStmt().get().isIfStmt()){
                    IfStmt elseIFStmt=(IfStmt) ifStmt.getElseStmt().get();
                    NodeList<Statement> statements=new NodeList<>();
                    statements.add(elseIFStmt);
                    ifStmt.setElseStmt(new BlockStmt(statements));
                }
            }
        });
    }
    // P Else{If(C1){...}} S -> P Elif(C1){...} S
    public  static void rule8(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt -> {
            if(ifStmt.getElseStmt().isPresent()&&ifStmt.getElseStmt().get().isBlockStmt()){
                BlockStmt elseStmt=(BlockStmt) ifStmt.getElseStmt().get();
                if(elseStmt.getStatements().size()==1){
                    Statement statement=elseStmt.getStatement(0);
                    if(statement.isIfStmt()){
                        ifStmt.setElseStmt(statement);
                    }
                }
            }
        });
    }
    //P For(int i:I){B1} S -> P If(I.size()>0){For(int i:I){B1}} S
    //P For(int i:I){B1} S -> P If(I.size()<=0){Pass}Else{For(int i:I){B1}} S
    //P While(C1){B1} S -> P If(C1){While(C1){B1}} S
    //P While(C1){B1} S -> P If(!C1){Pass}Else{While(C1){B1}} S
    public static void rule9(Node n){
        n.findAll(ForEachStmt.class).forEach(forEachStmt -> {
            IfStmt newIF=new IfStmt();
            MethodCallExpr methodCallExpr=new MethodCallExpr(forEachStmt.getIterable(),"size");
//          newIF.setThenStmt(forEachStmt);
//          BinaryExpr binaryExprGen=new BinaryExpr(methodCallExpr,new IntegerLiteralExpr(0),BinaryExpr.Operator.GREATER);
//          newIF.setCondition(binaryExprGen);
            newIF.setThenStmt(new EmptyStmt());
            newIF.setElseStmt(forEachStmt.clone());
            BinaryExpr binaryExprLess=new BinaryExpr(methodCallExpr,new IntegerLiteralExpr("0"), BinaryExpr.Operator.LESS_EQUALS);
            newIF.setCondition(binaryExprLess);
            forEachStmt.replace(newIF);
        });
        n.findAll(WhileStmt.class).forEach(whileStmt -> {
            IfStmt newIF=new IfStmt();
//          newIF.setThenStmt(whileStmt.clone());
//          newIF.setCondition(whileStmt.getCondition());
            newIF.setThenStmt(new EmptyStmt());
            newIF.setElseStmt(whileStmt.clone());
            newIF.setCondition(new UnaryExpr(whileStmt.getCondition(),UnaryExpr.Operator.LOGICAL_COMPLEMENT));
            whileStmt.replace(newIF);
        });
    }
    //  P While(C1){B1} S -> P While(True){If(!C1){Break}B1} S
    public static void rule10(Node n){
        n.findAll(WhileStmt.class).forEach(whileStmt -> {
            IfStmt newIF=new IfStmt();
            newIF.setCondition(new UnaryExpr(whileStmt.getCondition(),UnaryExpr.Operator.LOGICAL_COMPLEMENT));
            newIF.setThenStmt(new BreakStmt(" "));
            whileStmt.setCondition(new BooleanLiteralExpr(true));
            if(whileStmt.getBody().isBlockStmt()){
                NodeList<Statement>list=((BlockStmt)whileStmt.getBody()).getStatements();
                list.add(0,newIF);
            }else{
                BlockStmt blockStmt=new BlockStmt();
                blockStmt.getStatements().add(0,newIF);
                blockStmt.getStatements().add(1,whileStmt.getBody());
                whileStmt.setBody(blockStmt);
            }
        });
    }

    //P If(C1){B1 J1} S -> P While(C1){B1 J1} s
    //P If(C1){B1} S -> P While(C1){B1 break} S
    //Note: no continue or break is in B1
    public static void rule11(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt -> {
            VoidVisitor voidVisitor=new VoidVisitor();
            voidVisitor.visit(ifStmt.getThenStmt(),null);
            if(voidVisitor.getStrCFS().contains("Return")){
                WhileStmt whileStmt=new WhileStmt(ifStmt.getCondition(),ifStmt.getThenStmt());
                ifStmt.replace(whileStmt);
            }else{
                if(ifStmt.getThenStmt().isBlockStmt()){
                    NodeList<Statement>list=((BlockStmt)ifStmt.getThenStmt()).getStatements();
                    list.add(new BreakStmt(" "));
                    WhileStmt whileStmt=new WhileStmt(ifStmt.getCondition(),ifStmt.getThenStmt());
                    ifStmt.replace(whileStmt);
                }else{
                    BlockStmt blockStmt=new BlockStmt();
                    blockStmt.getStatements().add(ifStmt.getThenStmt());
                    blockStmt.getStatements().add(new BreakStmt(" "));
                    WhileStmt whileStmt=new WhileStmt(ifStmt.getCondition(),blockStmt);
                    ifStmt.replace(whileStmt);
                }
            }
        });
    }
    //P Else{B1} S -> P Elif(True){B1} S
    public static void rule12(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt -> {
            if(ifStmt.getElseStmt().isPresent()){
                Statement elseStmt=ifStmt.getElseStmt().get();
                if(!(elseStmt instanceof IfStmt)){
                    IfStmt newIF=new IfStmt();
                    newIF.setCondition(new BooleanLiteralExpr(true));
                    newIF.setThenStmt(elseStmt.clone());
                    ifStmt.setElseStmt(newIF);
                }
            }
        });
    }

    //P IF(C1){B1} ELSE IF(C2){B2} S --> IF(C1){B1} ELSE{IF(C2){}} S
    public static void formatElif2ElseIf(Node n){
        n.findAll(IfStmt.class).forEach(ifStmt -> {
            if(ifStmt.getElseStmt().isPresent()){
                Statement elseStmt=ifStmt.getElseStmt().get();
                if(elseStmt instanceof  IfStmt){
                    NodeList<Statement> statements = new NodeList<>();
                    statements.add(elseStmt);
                    BlockStmt blockStmt=new BlockStmt();
                    ifStmt.setElseStmt(blockStmt);
                    elseStmt.setParentNode(blockStmt);
                    blockStmt.setStatements(statements);
                }
            }
        });
    }

    //if(C1){B1}else{B2} --> if(not C1){B2}else{B1}
    public static void branchChange(IfStmt node){
        conditionChange(node);
        BlockStmt thenN = (BlockStmt) node.getThenStmt();
//        System.out.println(thenN.getChildNodes().size());
        if(node.getElseStmt().isPresent()){
            node.setThenStmt(node.getElseStmt().get());
        }else{
            node.setThenStmt(new BlockStmt());
            ((BlockStmt) node.getThenStmt()).addStatement(new EmptyStmt());
        }
        node.setElseStmt(thenN);
    }

    //if(C1){} --> if(!C1){}
    public static void conditionChange(IfStmt node){
        Expression cond = node.getCondition();
//        System.out.println(cond.toString());
        if(cond instanceof BooleanLiteralExpr){
            if(((BooleanLiteralExpr)cond).getValue()) {
                node.setCondition(new BooleanLiteralExpr(false));
            }else{
                node.setCondition(new BooleanLiteralExpr(true));
            }
        }else {
            Expression newCond = new UnaryExpr(new EnclosedExpr(cond), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
            node.setCondition(newCond);
        }
    }

    public static void insertNode(Node parent, Statement n, int index){
        NodeList<Statement> statements;
        if(parent instanceof SwitchEntry)
            statements = ((SwitchEntry)parent).getStatements();
        else
            statements = ((BlockStmt)parent).getStatements();
        statements.add(index, n);
        index = index +1;
        for (int i = index; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            statements.remove(i);
            statements.add(i, stmt);
        }
    }


    public static IfStmt addNewIf(Node node, int index){
        IfStmt newIf = new IfStmt(new BooleanLiteralExpr(true),new BlockStmt(),null);
        ((BlockStmt) newIf.getThenStmt()).addStatement(new EmptyStmt());
        insertNode(node, newIf, index);
        return newIf;
    }

    public static ForStmt addNewFor(Node node, int index){
//        ForStmt newFor = new ForStmt(null, new NodeList<>(), new BooleanLiteralExpr(), new NodeList<>(), new BlockStmt());
//        newFor.getBody().asBlockStmt().addStatement(new EmptyStmt());
        JavaParser javaParser=new JavaParser();
        Optional<CompilationUnit> result2 = javaParser.parse("public class Test{\n" +
                "public String foo(int i){\n" +
                "for (int random = 0; random<0;random++ ) {\n" +
                "            ;\n" +
                "        }\n}\n}\n").getResult();
        ForStmt newFor = result2.get().findAll(ForStmt.class).get(0);
        insertNode(node, newFor, index);
        return newFor;
    }

    public static WhileStmt addNewWhile(Node node, int index){
//        WhileStmt newWhile = new WhileStmt(null, new BooleanLiteralExpr(), new BlockStmt());
//        newWhile.getBody().asBlockStmt().addStatement(new EmptyStmt());
        JavaParser javaParser=new JavaParser();
        Optional<CompilationUnit> result2 = javaParser.parse("public class Test{\n" +
                "public String foo(int i){\n" +
                "while (true){\n" +
                "            break;\n" +
                "        }\n}\n}\n").getResult();
        WhileStmt newWhile = result2.get().findAll(WhileStmt.class).get(0);
        insertNode(node, newWhile, index);
        return newWhile;
    }

    public static ForEachStmt addNewForEach(Node node, int index){
//        ForEachStmt newForEach = new ForEachStmt(null, new VariableDeclarationExpr(), new NameExpr(), new BlockStmt());
//        newForEach.getBody().asBlockStmt().addStatement(new EmptyStmt());
        JavaParser javaParser=new JavaParser();
        Optional<CompilationUnit> result2 = javaParser.parse("public class Test{\n" +
                "public String foo(int i){\n" +
                "for (int random: new int[]{}) {;}}\n}\n").getResult();
        ForEachStmt newForEach = result2.get().findAll(ForEachStmt.class).get(0);
        insertNode(node, newForEach, index);
        return newForEach;
    }



    public static void execRule(Node n,int rule_id) {
        switch (rule_id) {
            case 1:
                rule1(n);
                break;
            case 2:
                rule2(n);
                break;
            case 3:
                rule3(n);
                break;
            case 4:
                rule4(n);
                break;
            case 5:
                rule5(n);
                break;
            case 6:
                rule6(n);
                break;
            case 7:
                rule7(n);
                break;
            case 8:
                rule8(n);
                break;
            case 9:
                rule9(n);
                break;
            case 10:
                rule10(n);
                break;
            case 11:
                rule11(n);
                break;
            case 12:
                rule12(n);
                break;
            case 122:
                rule122(n);
                break;
            case 13:
                formatElif2ElseIf(n);
                break;
        }
    }
}

