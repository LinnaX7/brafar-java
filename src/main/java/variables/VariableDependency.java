package variables;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import program.block.BlockNode;

import java.util.*;

public class VariableDependency extends VoidVisitorAdapter<Void> {
    Stack<Set<Variable>> ctrlStack=new Stack<>();
    int top=0;
    int prev_top=0;
    boolean delay=false;
    boolean delayForever=false;
    VariableBuilder variableBuilder;
    GraphAdjacencyMatrix matrix;
    //传入node
    //variableBuilder
    //matrix=new GraphAdjacencyMatrix(variableBuilder.getAllSize())
    public VariableDependency(Node node, VariableBuilder variableBuilder, GraphAdjacencyMatrix matrix){
        this.variableBuilder=variableBuilder;
        this.matrix=matrix;
        if(node instanceof MethodDeclaration)
            this.visit((MethodDeclaration) node,null);
        //add other ast node
    }

    public static GraphAdjacencyMatrix getGraphAdjacencyMatrixFromMethodD(MethodDeclaration methodDeclaration, VariableBuilder variableBuilder){
        GraphAdjacencyMatrix graphAdjacencyMatrix = new GraphAdjacencyMatrix(variableBuilder.getVariableList().size());
        return new VariableDependency(methodDeclaration, variableBuilder, graphAdjacencyMatrix).matrix;
    }

    public static GraphAdjacencyMatrix getGraphAdjacencyMatrixFromBlock(BlockNode blockNode, VariableBuilder variableBuilder){
        GraphAdjacencyMatrix graphAdjacencyMatrix = new GraphAdjacencyMatrix(variableBuilder.getVariableList().size());
        VariableDependency variableDependency = new VariableDependency(variableBuilder, graphAdjacencyMatrix);
        switch (blockNode.getBlockType()) {
            case EMPTY_BLOCK:
            case BASIC_BLOCK:
                for(Node node:blockNode.getTreeNodes()) {
                    if (node instanceof VariableDeclarator)
                        variableDependency.visit((VariableDeclarator) node, null);
                    else if (node instanceof ExpressionStmt)
                        variableDependency.visit((ExpressionStmt) node, null);
                }
                if(blockNode.getJumpBlock()!=null){
                    Node node = blockNode.getJumpBlock().getTreeNode();
                    if (node instanceof ContinueStmt)
                        variableDependency.visit((ContinueStmt) node, null);
                    else if (node instanceof BreakStmt)
                        variableDependency.visit((BreakStmt) node, null);
                    else if (node instanceof ReturnStmt)
                        variableDependency.visit((ReturnStmt) node, null);
                }
                break;
            case IF_BLOCK:
                variableDependency.visit((IfStmt) blockNode.getTreeNodes().get(0), null);
                break;
            case FOR_BLOCK:
                variableDependency.visit((ForStmt) blockNode.getTreeNodes().get(0), null);
                break;
            case IF_COND:
            case FOR_COMP:
            case FOREACH_ITER:
            case WHILE_COND:
            case FOR_UPDATE:
            case FOR_INIT:
                for(Node node:blockNode.getTreeNodes()) {
                    node.accept(variableDependency, null);
                }
                break;
            case FOREACH_BLOCK:
                variableDependency.visit((ForEachStmt) blockNode.getTreeNodes().get(0), null);
                break;
            case WHILE_BLOCK:
                variableDependency.visit((WhileStmt) blockNode.getTreeNodes().get(0), null);
                break;
            case WHILE_BODY:
            case FOR_BODY:
            case FOREACH_BODY:
            case IF_BODY:
                variableDependency.visit((BlockStmt) blockNode.getParentNode(), null);
                break;
        }
        return graphAdjacencyMatrix;
    }

    public VariableDependency(VariableBuilder variableBuilder, GraphAdjacencyMatrix matrix){
        this.variableBuilder=variableBuilder;
        this.matrix=matrix;
        //add other ast node
    }
    //variables which may be affected by control dependence in the exp
    public void getAffectedVariableFromExp(Expression exp, List<Variable> variableList){
        if(exp instanceof VariableDeclarationExpr){
            for(VariableDeclarator vd:((VariableDeclarationExpr) exp).getVariables()){
                if(vd.getInitializer().isPresent()){
                    variableList.add(variableBuilder.getVariable(vd.getName().getIdentifier(),vd));
                }
            }
        }else if(exp instanceof AssignExpr){
            Set<Variable>target=new LinkedHashSet<>();
            variableBuilder.getVariablesFromNode(((AssignExpr)exp).getTarget(),target);
            variableList.addAll(target);
        }else if(exp instanceof UnaryExpr){
            if(((UnaryExpr)exp).getOperator().equals(UnaryExpr.Operator.POSTFIX_DECREMENT)||
                    ((UnaryExpr)exp).getOperator().equals(UnaryExpr.Operator.POSTFIX_INCREMENT)||
                    ((UnaryExpr)exp).getOperator().equals(UnaryExpr.Operator.PREFIX_INCREMENT)||
                    ((UnaryExpr)exp).getOperator().equals(UnaryExpr.Operator.PREFIX_DECREMENT)
            ){
                Set<Variable>unary=new LinkedHashSet<>();
                variableBuilder.getVariablesFromNode(exp,unary);
                variableList.addAll(unary);
            }
        }
    }
    public void addCtrlDependency(Expression exp){
        List<Variable>affectedVariables=new ArrayList<>();
        getAffectedVariableFromExp(exp,affectedVariables);
        for(Variable l:affectedVariables){
            int li=variableBuilder.getIndex(l);
            for(Set<Variable>variables:ctrlStack){
                for(Variable r:variables){
                    int ri=variableBuilder.getIndex(r);
                    matrix.addEdge(li,ri);
                }
            }
        }
    }
    public void Stack_push(Set<Variable> variables){
        ctrlStack.push(variables);
        top++;
    }
    public void Stack_pop(){
        if(!delay){
            if(top!=prev_top) {
                ctrlStack.pop();
                top--;
            }
        }
        if(delayForever){
            prev_top=top;
        }
        delay=false;
        delayForever=false;
    }
    @Override
    public void visit(MethodDeclaration methodDeclaration,Void arg){
        if(methodDeclaration.getBody().isPresent()){
            visit(methodDeclaration.getBody().get(),arg);
        }
    }
    @Override
    public void visit(IfStmt ifStmt, Void arg){
        ifStmt.getCondition().accept(this,arg);
        //add control dependency
        Set<Variable> cond=new LinkedHashSet<>();
        variableBuilder.getVariablesFromNode(ifStmt.getCondition(),cond);
        Stack_push(cond);
        ifStmt.getThenStmt().accept(this,arg);
        if(ifStmt.getElseStmt().isPresent()){
            ifStmt.getElseStmt().get().accept(this,arg);
        }
        Stack_pop();
        super.visit(ifStmt,arg);
    }

    @Override
    public void visit(WhileStmt whileStmt, Void arg){
        whileStmt.getCondition().accept(this,arg);
        //add control dependency
        Set<Variable> cond=new LinkedHashSet<>();
        variableBuilder.getVariablesFromNode(whileStmt.getCondition(),cond);
        Stack_push(cond);
        whileStmt.getBody().accept(this,arg);
        Stack_pop();
        super.visit(whileStmt,arg);
    }
    @Override
    public void visit(ForEachStmt forEachStmt, Void arg){
        //add control dependency
        Set<Variable> cond=new LinkedHashSet<>();
        variableBuilder.getVariablesFromNode(forEachStmt.getIterable(),cond);
        Stack_push(cond);
        forEachStmt.getBody().accept(this, arg);
        Stack_pop();
        //add data dependency
        VariableDeclarator vd=forEachStmt.getVariableDeclarator();
        Variable l=variableBuilder.getVariable(vd.getName().getIdentifier(),vd);
        int li=variableBuilder.getIndex(l);
        Set<Variable> variables=new LinkedHashSet<>();
        variableBuilder.getVariablesFromNode(forEachStmt.getIterable(),variables);
        for(Variable r:variables){
            int ri=variableBuilder.getIndex(r);
            matrix.addEdge(li,ri);
        }
        super.visit(forEachStmt,arg);
    }
    @Override
    public void visit(ForStmt forStmt, Void arg){
        forStmt.getInitialization().accept(this,arg);
        forStmt.getUpdate().accept(this,arg);
        if(forStmt.getCompare().isPresent())
            forStmt.getCompare().get().accept(this,arg);
        //add control dependency
        Set<Variable> cond=new LinkedHashSet<>();
        if(forStmt.getCompare().isPresent())
            variableBuilder.getVariablesFromNode(forStmt.getCompare().get(),cond);
        Stack_push(cond);
        for(Expression exp:forStmt.getUpdate()){
            addCtrlDependency(exp);
        }
        forStmt.getBody().accept(this,arg);
        Stack_pop();
        super.visit(forStmt,arg);
    }
    @Override
    public void visit(BreakStmt breakStmt, Void arg){
        delay=true;
    }
    @Override
    public void visit(ContinueStmt continueStmt,Void arg){
        delay=true;
    }
    @Override
    public void visit(ReturnStmt returnStmt,Void arg){
        //add control dependency
        delay=true;
        delayForever=true;
        if(returnStmt.getExpression().isPresent()){
            Variable l=variableBuilder.getReturnVariable(returnStmt.getExpression().get());
            int li=variableBuilder.getIndex(l);
            for(Set<Variable>variables:ctrlStack){
                for(Variable r:variables){
                    int ri=variableBuilder.getIndex(r);
                    matrix.addEdge(li,ri);
                }
            }
        }
        //add data dependency
        Set<Variable>variables=new LinkedHashSet<>();
        variableBuilder.getVariablesFromNode(returnStmt,variables);
        if(returnStmt.getExpression().isPresent()) {
            Variable l=variableBuilder.getReturnVariable(returnStmt.getExpression().get());
            int li=variableBuilder.getIndex(l);
            for(Variable r:variables){
                int ri=variableBuilder.getIndex(r);
                matrix.addEdge(li,ri);
            }
        }
    }
    @Override
    public void visit(ExpressionStmt expressionStmt,Void arg){
        //add control dependency
        super.visit(expressionStmt, arg);
        if(ctrlStack.empty()){
            return;
        }
        Expression exp=expressionStmt.getExpression();
        addCtrlDependency(exp);
    }
    @Override
    public void visit(VariableDeclarator vd,Void arg){
        //add data dependency
        if(vd.getInitializer().isPresent()){
            Variable l=variableBuilder.getVariable(vd.getName().getIdentifier(),vd);
            int li=variableBuilder.getIndex(l);
            Set<Variable>rightV=new LinkedHashSet<>();
            variableBuilder.getVariablesFromNode(vd.getInitializer().get(),rightV);
            for(Variable r:rightV){
                int ri=variableBuilder.getIndex(r);
                matrix.addEdge(li,ri);
            }
        }
    }

    @Override
    public void visit(AssignExpr assignExpr,Void arg){
        //add data dependency
        Expression left=assignExpr.getTarget();
        Expression right=assignExpr.getValue();

        Set<Variable>leftV=new LinkedHashSet<>();
        variableBuilder.getVariablesFromNode(left,leftV);
        Set<Variable>rightV=new LinkedHashSet<>();
        variableBuilder.getVariablesFromNode(right,rightV);
        for(Variable l:leftV){
            int li=variableBuilder.getIndex(l);
            for(Variable r:rightV){
                int ri=variableBuilder.getIndex(r);
                matrix.addEdge(li,ri);
            }
        }
    }
}
