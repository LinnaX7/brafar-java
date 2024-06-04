package variables;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import sar.repair.utils.Utils;

import java.util.*;

public class VariableBuilder extends VoidVisitorAdapter<Void>{
    //matrix
    GraphAdjacencyMatrix matrix;
    //get index from variable
    Map<Variable,Integer> VI;
    //get variable from index
    List<Variable>variableList;
    //used for param match
    List<Variable>paramList;
    int paramSize;
    //map <String name, Variable>  used for different names
    private HashMap<String,Variable> variableMap;
    private Set<String>sameName;
    //abstract for return
    List<Pair<Expression,Variable>>EVPairs;
//    Map<Expression,Variable>E2VMap; //get return variable from a expression
//    Map<Variable,Expression>V2EMap;//get expression from a return value
    List<Variable>returnList; //return variables
    //get variableDeclarator
    public Map<Variable,VariableDeclarator>V2V;
    MethodDeclaration methodDeclaration;

    //<scope index>
    //Map<Node,Integer> SI;
    List<Pair<Node,Integer>>SI;
    List<Node>scopeList;

    int index;
    int return_Index;
    int scope_index;
    List<ReturnStmt> returnStmts;
    public VariableBuilder(MethodDeclaration methodDeclaration){
        this.methodDeclaration=methodDeclaration;
        init();
        this.visit(methodDeclaration,null);
        matrix=new GraphAdjacencyMatrix(variableList.size());

        VariableDependency vd=new VariableDependency(methodDeclaration,this,matrix);
//        System.out.println("----------------------------Variable-------------------------------");
//        for(Map.Entry<Variable,VariableDeclarator>entry: V2V.entrySet()){
//            System.out.println(entry.getKey().getName()+": "+entry.getValue().toString());
//        }
//        System.out.println("-------------------------------------------------------------------");
//        for(Map.Entry<Variable,Integer>entry:VI.entrySet()){
//            System.out.println(entry.getKey().getName()+": "+entry.getValue());
//        }
//        matrix.printGraph();

        setRank();
    }
    public void updateV2V(Variable var,VariableDeclarator variableDeclarator){
        V2V.remove(var);
        V2V.put(var,variableDeclarator);
    }
    private void init(){
        returnStmts=new ArrayList<>();
        VI=new HashMap<>();
        variableList=new ArrayList<>();
        paramList=new ArrayList<>();
        paramSize=0;
        variableMap=new HashMap<>();
        sameName=new LinkedHashSet<>();
        EVPairs=new ArrayList<>();
//        E2VMap=new HashMap<>();
//        V2EMap=new HashMap<>();
        returnList=new ArrayList<>();
        V2V=new HashMap<>();
        //SI=new HashMap<>();
        SI=new ArrayList<>();
        scopeList=new ArrayList<>();
        index=0;
        return_Index=0;
        scope_index=0;
    }
    public  List<ReturnStmt> getReturnStmts() {
        return returnStmts;
    }
    public void addVariable(Variable var){
        variableList.add(var);
    }
    //add variable to Variable-to-Index
    public void insertVI(Variable var){
        if(!VI.containsKey(var)){
            VI.put(var,index);
            variableList.add(var);
            index++;
        }
    }
    public void insertScopeMap(Node node){
        SI.add(new Pair<>(node,scope_index));
        scopeList.add(node);
        scope_index++;
    }
    @Override
    public void visit(ClassOrInterfaceDeclaration c,Void arg){
        insertScopeMap(c);
        super.visit(c,arg);
    }
    @Override
    public void visit(MethodDeclaration m,Void arg){
        insertScopeMap(m);
        super.visit(m,arg);
    }
    @Override
    public void visit(BlockStmt b,Void arg){
        insertScopeMap(b);
        super.visit(b,arg);
    }
    @Override
    public void visit(DoStmt d,Void arg){
        insertScopeMap(d);
        super.visit(d,arg);
    }
    @Override
    public void visit(SwitchStmt s,Void arg){
        insertScopeMap(s);
        super.visit(s,arg);
    }
    @Override
    public void visit(TryStmt t,Void arg){
        insertScopeMap(t);
        super.visit(t,arg);
    }
    @Override
    public void visit(ThrowStmt t,Void arg){
        insertScopeMap(t);
        super.visit(t,arg);
    }
    @Override
    public void visit(Parameter parameter, Void arg){
        Variable var = new Variable(parameter.getName().getIdentifier(),parameter.getType(), Variable.SpecialType.PARAM,Variable.getScope(parameter));
        insertVI(var);
        paramList.add(var);
        paramSize++;
        variableMap.put(parameter.getName().getIdentifier(),var);
        super.visit(parameter, arg);
    }
    @Override
    public void visit(ReturnStmt returnStmt,Void arg){
        if(returnStmt.getExpression().isPresent()){
            returnStmts.add(returnStmt);
            Variable var=new Variable("r"+return_Index,methodDeclaration.getType(), Variable.SpecialType.RETURN,Variable.getScope(returnStmt));
            return_Index++;
            returnList.add(var);
            insertVI(var);
            variableMap.put(var.getName(),var);
            EVPairs.add(new Pair<>(returnStmt.getExpression().get(),var));
//            E2VMap.put(returnStmt.getExpression().get(),var);
//            V2EMap.put(var,returnStmt.getExpression().get());
        }
    }
    @Override
    public void visit(IfStmt ifStmt, Void arg){
        insertScopeMap(ifStmt);
        super.visit(ifStmt,arg);
    }
    @Override
    public void visit(WhileStmt whileStmt,Void arg){
        insertScopeMap(whileStmt);
        super.visit(whileStmt,arg);
    }
    @Override
    public void visit(ForStmt forStmt,Void arg){
        insertScopeMap(forStmt);
        super.visit(forStmt,arg);
    }
    @Override
    public  void visit(ForEachStmt forEachStmt,Void arg){
        insertScopeMap(forEachStmt);
        super.visit(forEachStmt,arg);
    }
    @Override
    public void visit(VariableDeclarator variableDeclarator, Void arg) {
        Variable var = new Variable(variableDeclarator.getName().getIdentifier(),
                variableDeclarator.getType(), Variable.SpecialType.LOCAL,Variable.getScope(variableDeclarator));
        insertVI(var);
        V2V.put(var,variableDeclarator);
        if(variableMap.containsKey(variableDeclarator.getName().getIdentifier()))
            sameName.add(variableDeclarator.getName().getIdentifier());
        else{
            variableMap.put(variableDeclarator.getName().getIdentifier(),var);
        }
        super.visit(variableDeclarator, arg);
    }
    public Collection<VariableDeclarator> getVariableDeclarators(){
        return V2V.values();
    }
    public VariableDeclarator getVariableDeclarator(Variable v){
        return V2V.get(v);
    }
    public void setRank(){
        for(Variable r:returnList){
            Queue<Variable>variables=new LinkedList<>();
            variables.add(r);
            int tempRank=0;
            r.setRank(tempRank);
            tempRank++;
            while(!variables.isEmpty()){
                List<Variable> rely=getRely(variables.peek());
                variables.remove();
                for(Variable v:rely){
                    if(tempRank<v.getRank())
                        v.setRank(tempRank);
                    if(!v.isVisit()){
                        variables.add(v);
                        v.visited();
                    }
                }
                tempRank++;
            }
            for(Variable v:variableList){
                v.resetVisit();
            }
        }
    }
    public List<Variable> getRankVariables(int rank){
        List<Variable> rankList=new ArrayList<>();
        for(Variable v:variableList){
            if(v.getRank()<=rank){
                rankList.add(v);
            }
        }
        return rankList;
    }
    //get all variables in the node
    public void getVariablesFromNode(Node node,Set<Variable>variableList){
        List<SimpleName>simpleNames= Utils.getVariableNameFromNode(node);
        for(SimpleName simpleName:simpleNames){
            Variable v=getVariable(simpleName.getIdentifier(),simpleName);
            if(v!=null){
                variableList.add(v);
            }
        }
    }
    public void getVariablesFromNode(Node node,List<Variable>variableList){
        List<SimpleName>simpleNames= Utils.getVariableNameFromNode(node);
        for(SimpleName simpleName:simpleNames){
            Variable v=getVariable(simpleName.getIdentifier(),simpleName);
            if(v!=null){
                variableList.add(v);
            }
        }
    }
    public  boolean isIdentical(String name){
        return !sameName.contains(name);
    }
    public Variable getVariable(String name){
        return variableMap.get(name);
    }
    public Variable getVariableByIndex(String name,int scopeIndex){
        return getVariable(name,getScope(scopeIndex));
    }
    public Variable getVariable(String name,Node use){
        for(Variable variable:variableList){
            if(variable.getName().equals(name)&&Variable.isInclude(variable.getScope(),use)){
                return variable;
            }
        }
        return null;
    }
    public Variable getVariable(String name,int line){
        Node node=getNodeFormLine(line,methodDeclaration);
        if(node !=null)
            return getVariable(name,node);
        return null;
    }
    public Variable getVariable(int index){
        return variableList.get(index);
    }
    public Node getNodeFormLine(int line,Node node){
        if(node.getBegin().isPresent()){
            if(node.getBegin().get().line==line){
                return node;
            }
        }
        for(Node child:node.getChildNodes()){
            Node r=getNodeFormLine(line,child);
            if(r!=null){
                return r;
            }
        }
        return null;
    }
    public Expression getReturnExpression(Variable v){
        for(Pair<Expression,Variable>pair:EVPairs){
            if(pair.second==v){
                return pair.first;
            }
        }
        return null;
        //return V2EMap.get(v);
    }

    public void putReturnExpression(Variable var, Expression expression){
        EVPairs.add(new Pair<>(expression,var));
    }
    public Variable getReturnVariable(Expression exp){
        //return E2VMap.get(exp);
        for(Pair<Expression,Variable>pair:EVPairs){
            if(pair.first==exp){
                return pair.second;
            }
        }
        return null;
    }
    public int getIndex(Variable variable){
        return VI.get(variable);
    }
    public int getAllSize(){
        return VI.size();
    }
    public int getParamSize(){
        return paramSize;
    }
    public List<Variable> getVariableList(){
        return variableList;
    }
    public List<Variable> getParamList(){
        return paramList;
    }
    public List<Variable> getReturnList(){return returnList;}
    public List<Integer> getRely(int n){
        return matrix.getRely(n);
    }
    public List<Integer> getBeRelied(int n){
        return matrix.getBeRelied(n);
    }
    public List<Variable> getRely(Variable var){
        List<Integer> relyIndex=getRely(getIndex(var));
        List<Variable> relyVariables=new ArrayList<>();
        for(Integer i:relyIndex){
            relyVariables.add(variableList.get(i));
        }
        return relyVariables;
    }
    public List<Variable> getRelied(Variable var){
        List<Integer> reliedIndex=getBeRelied(getIndex(var));
        List<Variable> reliedVariables=new ArrayList<>();
        for(Integer i:reliedIndex){
            reliedVariables.add(variableList.get(i));
        }
        return reliedVariables;
    }
    public Integer getScopeIndex(Node scope){
        for(Pair<Node,Integer> pair:SI){
           if(pair.first==scope){
               return pair.second;
           }
        }
        return -1;
    }
    public Node getScope(int index){
        return scopeList.get(index);
    }
}
