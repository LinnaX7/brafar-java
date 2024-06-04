package cfs;

import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class VoidVisitor extends VoidVisitorAdapter {
    //
    private String strCFS="";


    @Override
    public void visit(IfStmt n, Object arg){
        if(arg==null)
            strCFS+="If_start,";
        visit(n.getThenStmt(),arg);
        if(arg==null)
            strCFS+="If_end,";
        if(n.hasElseBlock()||n.hasElseBranch()){
            if(n.getElseStmt().isPresent()){
                    if(n.getElseStmt().get().isIfStmt()){
                        strCFS+="Elif_start,";
                        visit(n.getElseStmt().get(),"ELIF");
                        strCFS+="Elif_end,";
                    }else{
                        strCFS+="Else_start,";
                        visit(n.getElseStmt().get(),arg);
                        strCFS+="Else_end,";
                    }
            }
        }
    }
    public void visit(SwitchStmt switchStmt,Object arg){
        strCFS+="Switch_start,";
        for(SwitchEntry switchEntry: switchStmt.getEntries()){
            strCFS+="Case_start";
            for(Statement statement:switchEntry.getStatements()){
                visit(statement, arg);
            }
            strCFS+="Case_end";
        }
        strCFS+="Switch_end,";
    }
    public void visit(ForStmt forStmt,Object arg){
        strCFS+="For_start,";
        visit(forStmt.getBody(),null);
        strCFS+="For_end,";
    }
    public void visit(WhileStmt whileStmt,Object arg){
        strCFS+="While_start,";
        visit(whileStmt.getBody(),null);
        strCFS+="While_end,";
    }
    public void visit(ContinueStmt continueStmt,Object arg){
        strCFS+="Continue,";
    }
    public void visit(BreakStmt breakStmt,Object arg){
        strCFS+="Break,";
    }
    public void visit(ReturnStmt returnStmt,Object arg){
        strCFS+="Return,";
    }
    public void visit(ExpressionStmt expressionStmt,Object arg){
    }

    public void visit (BlockStmt blockStmt, Object arg){
        for(Statement statement:blockStmt.getStatements()){
            visit(statement, arg);
        }
    }

    public void visit(Statement statement,Object arg){
        if(statement instanceof  BlockStmt){
            visit((BlockStmt) statement,arg);
        }else if(statement instanceof IfStmt){
            visit((IfStmt) statement,arg);
        }else if(statement instanceof SwitchStmt){
            visit((SwitchStmt) statement,arg);
        }else if(statement instanceof ForStmt){
            visit((ForStmt) statement,arg);
        }else if(statement instanceof WhileStmt){
            visit((WhileStmt) statement,arg);
        }else if(statement instanceof ContinueStmt){
            visit((ContinueStmt) statement,arg);
        }else if(statement instanceof BreakStmt){
            visit((BreakStmt) statement,arg);
        }else if(statement instanceof ReturnStmt){
            visit((ReturnStmt) statement,arg);
        }else if(statement instanceof  ExpressionStmt){
            visit((ExpressionStmt) statement,arg);
        }
    }



    public String getStrCFS(){
        if(strCFS.equals(""))
            return strCFS;
        return strCFS.substring(0,strCFS.length()-1);
    }
}
