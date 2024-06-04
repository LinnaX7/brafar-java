package variables;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import program.MethodBuilder;
import program.block.BlockNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Variable {
    private String name;
    private final Type type;
    private final ArrayList<Object> inValues;
    private final ArrayList<Object> values;//length = blockList.length, [ArrayList<>,null,[],[],[],]
    private int define;
    private final Set<Integer> uses;
    private final Set<Integer> returnUses;
    private final HashMap<Integer,Object> lineValues;
    private int rank=Integer.MAX_VALUE;
    private boolean isVisit=false;
    private int defUseMark;//blockMark

    public static boolean isTypeEqual(Type buggy,Type correct){
        if(buggy.isPrimitiveType()&&correct.isPrimitiveType()){
            ClassOrInterfaceType t1= buggy.asPrimitiveType().getType().toBoxedType();
            ClassOrInterfaceType t2=correct.asPrimitiveType().getType().toBoxedType();
            if(t1.getName().toString().equals(t2.getName().toString())){
                return true;
            }else{
                return !t1.getName().toString().equals("Boolean") && !t2.getName().toString().equals("Boolean");
            }
        }else{
            return buggy.equals(correct);
        }
    }
    public int getDefUseMark() {
        return defUseMark;
    }

    public void addDefUseMark(int mark){
        this.defUseMark += mark;
    }

    //变量特殊类型
    public enum SpecialType{
        PARAM,RETURN,LOCAL
    }
    public SpecialType specialType;
    //变量作用域
    private  Node scope;
    public void setRank(int rank){
        this.rank=rank;
    }
    public int getRank(){
        return rank;
    }
    public boolean isVisit(){
        return isVisit;
    }
    public void visited(){
        isVisit=true;
    }
    public void resetVisit(){
        isVisit=false;
    }
    // Overriding equals() to compare two Variables
    @Override
    public boolean equals(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }
        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Variable)) {
            return false;
        }

        Variable v = (Variable) o;
        return this.name.equals(v.getName())
                && this.scope==v.getScope();
    }

    public String getName() {
        return name;
    }

    public Node getScope(){return scope;}

    public static Node getScope(Node node){
        if(node instanceof ClassOrInterfaceDeclaration
                ||node instanceof MethodDeclaration
                ||node instanceof IfStmt
                ||node instanceof BlockStmt
                ||node instanceof ForStmt
                ||node instanceof ForEachStmt
                ||node instanceof DoStmt
                ||node instanceof SwitchStmt
                ||node instanceof TryStmt
                ||node instanceof ThrowStmt
                ||node instanceof WhileStmt){
            return node;
        }
        if(node.getParentNode().isEmpty()){
            return null;
        }
        return getScope(node.getParentNode().get());
    }
    //check the scopes of two variables
    public static boolean isInclude(Node def,Node use){
        if(def==use){
            return true;
        }
        if(use.getParentNode().isEmpty()){
            return false;
        }
        return isInclude(def,use.getParentNode().get());
    }
    public boolean isParam(){
        return specialType.equals(SpecialType.PARAM);
    }

    public Type getType() {
        return type;
    }

    public ArrayList<Object> getInValues() {
        return inValues;
    }

    public Object getLineValue(int lineIndex) {
        return lineValues.getOrDefault(lineIndex, null);
    }

    public Variable(String name, Type type, SpecialType specialType, Node scope){
        this.name = name;
        this.type = type;
        this.specialType=specialType;
        values = new ArrayList<>();
        inValues = new ArrayList<>();
        uses=new LinkedHashSet<>();
        returnUses = new LinkedHashSet<>();
        lineValues = new HashMap<>();
        define = -1;

        this.scope = scope;

        this.defUseMark = 0;
    }

    public void putLineValues(int index, Object value) {
        lineValues.put(index,value);
    }

    public HashMap<Integer, Object> getLineValues() {
        return lineValues;
    }



    public void addValues(Object var){
        values.add(var);
    }

    public void addInValues(Object var){
        inValues.add(var);
    }

    public void addValuesFromIndexes(ArrayList<Integer> indexes, ArrayList<Integer> inIndexes){
        if(inIndexes.size()== 0 && indexes.size()==0){
            inValues.add(null);
            addValues(null);
            return;
        }
        if(inIndexes.size()==0){
            inValues.add(null);
            addValuesFromIndexes(indexes);
            return;
        }
        if(indexes.size()==0){
            values.add(null);
            addInValuesFromIndexes(inIndexes);
            return;
        }
        addInValuesFromIndexes(inIndexes);
        addValuesFromIndexes(indexes);

    }

    public void addValuesFromIndexes(ArrayList<Integer> indexes){
        if(indexes.size()>1){
            ArrayList<Object> values = new ArrayList<>();
            for (Integer index : indexes) {
                if(index != -1) {
                    values.add(this.getLineValue(index));
                }else{
                    values.add(null);
                }
            }
            this.addValues(values);
        }else{
            this.addValues(this.getLineValue(indexes.get(0)));
        }
    }

    public void addInValuesFromIndexes(ArrayList<Integer> indexes){
        if(indexes.size()>1){
            ArrayList<Object> values = new ArrayList<>();
            for (Integer index : indexes) {
                if(index != -1) {
                    values.add(this.getLineValue(index));
                }else{
                    values.add(null);
                }
            }
            this.addInValues(values);
        }else{
            this.addInValues(this.getLineValue(indexes.get(0)));
        }
    }

    public void setDefine(int define){
        this.define=define;
    }
    public void setUses(int use){
        uses.add(use);
    }

    public void setReturnUses(int uses){
        returnUses.add(uses);
    }

    public Set<Integer> getReturnUses() {
        return returnUses;
    }

    public Set<Integer> getUses(){
        return uses;
    }
    public int getDefine(){
        return define;
    }
    public ArrayList<Object> getValues() {
        return values;
    }

    public boolean isDEA(Variable var){
        for (int i = 0; i < values.size(); i++) {
            if(values.get(i) == null){
                if(i>=var.getValues().size()||var.getValues().get(i)!=null)
                    return false;
                else
                    continue;
            }
            if (var.getValues().get(i) == null) {
                return false;
            }
            if(!values.get(i).equals(var.getValues().get(i))){
                return false;
            }
        }
        return true;
    }

    public boolean isDUA(Variable var){
        if(define != var.define)
            return false;
        return uses.equals(var.uses);
    }
    public void setScope(Node scope){
        this.scope=scope;
    }
    public  void setName(String name){
        this.name=name;
    }
}


