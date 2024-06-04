package cfs;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


public class CFSVisitor extends VoidVisitorAdapter<Object> {
    private String strCFS=""; //
    private boolean isFuncName; //
    private boolean isFirstNode;//
    private int cfsNums;

    public CFSVisitor(MethodDeclaration methodDeclaration){
        isFuncName = isFirstNode = false;
        cfsNums = 0;
        visit(methodDeclaration, null);
    }

    public int getCfsNums() {
        return cfsNums;
    }

    /**
     * @methodsName: addCFSNode
     * @description:
     * @param:
     */
    private void addCFSNode(String node){
        //检查是否为class或者function
        if(isFuncName){
            strCFS += node;
            strCFS += "():";
            isFuncName = false;
            isFirstNode = true;
        }else if(isFirstNode){
            strCFS += node;
            isFirstNode = false;
        }else{
            strCFS += ",";
            strCFS += node;
        }
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(ConstructorDeclaration n, Object arg) {
        //
        String tempName ="";
        tempName += n.getName();
        isFuncName = true;
        addCFSNode(tempName);
        super.visit(n, arg);
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(MethodDeclaration n, Object arg) {
        //
        String tempName ="";
        tempName += n.getName();
        isFuncName = true;
        addCFSNode(tempName);
        super.visit(n, arg);
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(BlockStmt n, Object arg){

        NodeList<Statement> StmtList = n.getStatements();
        for(Statement stmt: StmtList)
            visit(stmt,arg);
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(IfStmt n, Object arg){
        cfsNums++;
        addCFSNode("If_start");

        visit(n.getThenStmt(),arg);
        addCFSNode("If_end");

        if(n.hasElseBlock()||n.hasElseBranch()){
            if(n.getElseStmt().isPresent()){
                addCFSNode("Else_start");
                visit(n.getElseStmt().get(),arg);
                addCFSNode("Else_end");
            }
        }
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(SwitchStmt n, Object arg){
        cfsNums++;
        addCFSNode("Switch_start");
        NodeList<SwitchEntry> EntryList = n.getEntries();
        for(SwitchEntry entry: EntryList){
            addCFSNode("Switch_Entry");
            //
            NodeList<Statement> StmtList = entry.getStatements();
            for(Statement stmt: StmtList)
                visit(stmt,arg);
        }
        addCFSNode("Switch_end");
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(ForStmt n, Object arg){
        cfsNums++;
        addCFSNode("For_start");
        visit(n.getBody(),arg);
        addCFSNode("For_end");
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(ForEachStmt n, Object arg) {
        cfsNums++;
        addCFSNode("ForEach_start");
        visit(n.getBody(),arg);
        addCFSNode("ForEach_end");
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(WhileStmt n, Object arg){
        cfsNums++;
        addCFSNode("While_start");

        visit(n.getBody(),arg);

        addCFSNode("While_end");
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(DoStmt n, Object arg){
        cfsNums++;
        //do-while循环结构
        addCFSNode("Do_start");

        visit(n.getBody(),arg);

        addCFSNode("Do_end");
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(ContinueStmt n, Object arg){
        cfsNums++;
        addCFSNode("Continue");
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(BreakStmt n, Object arg){
        cfsNums++;
        addCFSNode("Break");
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(ReturnStmt n, Object arg){
        cfsNums++;
        addCFSNode("Return");
    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(LabeledStmt n, Object arg)
    {

    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    public void visit(UnparsableStmt n, Object arg)
    {

    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(BlockComment n, Object arg) {

    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(final JavadocComment n, final Object arg) {

    }

    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    @Override
    public void visit(LineComment n, Object arg) {

    }
    /**
     * @methodsName: visit
     * @description:
     * @param:
     */
    public void visit(Statement statement,Object arg){
        if(statement instanceof  BlockStmt){
            visit((BlockStmt) statement,arg);
        }else if(statement instanceof IfStmt){
            visit((IfStmt) statement,arg);
        }else if(statement instanceof SwitchStmt){
            visit((SwitchStmt) statement,arg);
        }else if(statement instanceof ForStmt){
            visit((ForStmt) statement,arg);
        }else if(statement instanceof ForEachStmt){
            visit((ForEachStmt) statement,arg);
        }else if(statement instanceof WhileStmt){
            visit((WhileStmt) statement,arg);
        }else if(statement instanceof DoStmt){
            visit((DoStmt) statement,arg);
        }else if(statement instanceof ContinueStmt){
            visit((ContinueStmt) statement,arg);
        }else if(statement instanceof BreakStmt){
            visit((BreakStmt) statement,arg);
        }else if(statement instanceof ReturnStmt){
            visit((ReturnStmt) statement,arg);
        }
    }

    /**
     * @methodsName: getStrCFS
     * @description:
     * @return: string
     */
    public String getStrCFS(){
        return strCFS;
    }

}