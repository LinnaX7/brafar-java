package cfs.guider;

import cfs.Refactor;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;

import java.util.ArrayList;
import java.util.List;

public class CSNode{
    private final Node treeNode;
    private int tIndex;
    private int height;
    private CSNode parent;
    private CSType csType;
    private ArrayList<CSNode> children;//index, CSNo-tree
    private boolean isNew;
    private ArrayList<CSNode> ancestorFW;
    private ArrayList<CSNode> ancestors;
    private boolean hasDBeMatched;

    public enum CSType{
        IF_STMT,
        FOR_STMT,
        WHILE_STMT,
        ELSE_BRANCH,
        THEN_BRANCH,
        METHOD_DECLARATION,
        FOREACH_STMT,
        SWITCH_STMT,
        CASE_BRANCH
    }

    public void incTIndex(int tIndex) {
        this.tIndex += tIndex;
    }

    public void branchChange(){
        CSNode thenBranch = this.getChildren().get(0);
        CSNode elseBranch = null;
        if(this.getChildren().size()==2){
            elseBranch = this.getChildren().get(1);
        }
        this.getChildren().clear();
        Refactor.branchChange((IfStmt) this.getTreeNode());
        if(elseBranch == null)
            elseBranch = new CSNode(((IfStmt)this.getTreeNode()).getThenStmt(), this.height+1, CSType.THEN_BRANCH, this);
        thenBranch.setCsType(CSType.ELSE_BRANCH);
        elseBranch.setCsType(CSType.THEN_BRANCH);
//        ArrayList<CSNode> thenChildList = thenBranch.getChildren();
//        ArrayList<CSNode> elseChildList = elseBranch.getChildren();
//        thenBranch.setChildren(elseChildList);
//        elseBranch.setChildren(thenChildList);
        this.getChildren().add(elseBranch);
        this.getChildren().add(thenBranch);
    }

    public void setCsType(CSType csType) {
        this.csType = csType;
    }

    public CSNode(MethodDeclaration methodDeclaration){
        this.treeNode = getBlockNodeFromFW(methodDeclaration);
        this.height = 0;
        this.csType = CSType.METHOD_DECLARATION;
        this.parent = null;
        this.children = new ArrayList<>();
        this.tIndex = 0;
        this.ancestorFW = null;
        this.ancestors = null;
        this.isNew = false;
        this.hasDBeMatched = false;
        initAncestors();
        initAncestorFW();
        addChildren();
    }

    public boolean isHasDBeMatched() {
        return hasDBeMatched;
    }

    public void setHasDBeMatched(boolean hasDBeMatched) {
        this.hasDBeMatched = hasDBeMatched;
    }

    public CSNode(Node node, int height, CSType csType, CSNode parent){
        this.treeNode = node;
        this.height = height;
        this.csType = csType;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.tIndex = 0;
        this.ancestorFW = null;
        this.ancestors = null;
        this.isNew = false;
        initAncestors();
        initAncestorFW();
    }

    public CSNode(Node node, int height, CSType csType, CSNode parent, int tIndex){
        this.treeNode = node;
        this.height = height;
        this.csType = csType;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.tIndex = tIndex;
        this.ancestorFW = null;
        this.ancestors = null;
        this.isNew = false;
        initAncestors();
        initAncestorFW();
        addChildren();
    }


    public boolean isBranch(){
        return this.csType == CSType.ELSE_BRANCH || this.csType == CSType.THEN_BRANCH;
    }

    public boolean isBranchChild(){
        if(this.parent == null)
            return false;
        return this.parent.isBranch();
    }


    public boolean isLoopNode(){
        return this.csType == CSType.FOR_STMT || this.csType == CSType.WHILE_STMT || this.csType == CSType.FOREACH_STMT;
    }

    public void initAncestorFW(){
        if(this.parent == null){
            return;
        }
        if(getParent().isLoopNode() || getParent().getCsType() == CSType.SWITCH_STMT){
            if(ancestorFW==null){
                ancestorFW = new ArrayList<>();
            }
            ancestorFW.add(parent);
        }
        if(parent.ancestorFW != null){
            if(ancestorFW==null){
                ancestorFW = new ArrayList<>();
            }
            ancestorFW.addAll(parent.ancestorFW);
        }
    }

    public void initAncestors(){
        if(this.parent == null){
            return;
        }
        ancestors = new ArrayList<>();
        ancestors.add(parent);
        if(parent.ancestors != null){
            ancestors.addAll(parent.ancestors);
        }
    }

    public static Node getBlockNodeFromFW(Node node){
        List<Node> childrenNodes;
        childrenNodes = node.getChildNodes();
        for (Node child:childrenNodes) {
            if(child instanceof BlockStmt){
                return child;
            }
        }
        System.out.println("Wrong Get Block of node!!");
        return null;
    }

    public void visitTree(){
        int index = 0;
        for (Node child: this.treeNode.getChildNodes()) {
            if(child instanceof IfStmt){
                this.children.add(new CSNode(child, this.height+1, CSType.IF_STMT, this,index));
            }else if(child instanceof ForStmt){
                this.children.add(new CSNode(getBlockNodeFromFW(child), this.height+1, CSType.FOR_STMT, this, index));
            }else if(child instanceof ForEachStmt){
                this.children.add(new CSNode(getBlockNodeFromFW(child), this.height+1, CSType.FOREACH_STMT, this, index));
            }else if(child instanceof WhileStmt){
                this.children.add(new CSNode(getBlockNodeFromFW(child), this.height+1, CSType.WHILE_STMT, this, index));
            }else if(child instanceof SwitchStmt){
                this.children.add(new CSNode(child, this.height+1, CSType.SWITCH_STMT, this, index));
            }
            index += 1;
        }
    }


    public void visitIf(IfStmt n){
        CSNode thenBranch = new CSNode(n.getThenStmt().asBlockStmt(), this.height+1, CSType.THEN_BRANCH, this, 0);
        this.children.add(thenBranch);
        if(n.getElseStmt().isPresent()){
            CSNode elseBranch = new CSNode(n.getElseStmt().get(), this.height+1, CSType.ELSE_BRANCH, this, 1);
            this.children.add(elseBranch);
        }
    }

    public void visitSwitchStmt(SwitchStmt n){
        int i = 0;
        for(SwitchEntry switchEntry:n.getEntries()){
            CSNode caseBranch = new CSNode(switchEntry, this.height+1, CSType.CASE_BRANCH, this, i);
            i += 1;
            this.children.add(caseBranch);
        }
    }

    public void addChildren(){
        switch (this.csType){
            case IF_STMT:
                visitIf((IfStmt) this.treeNode);
                break;
            case SWITCH_STMT:
                visitSwitchStmt((SwitchStmt) this.treeNode);
                break;
            case CASE_BRANCH:
            case METHOD_DECLARATION:
            case FOR_STMT:
            case FOREACH_STMT:
            case WHILE_STMT:
            case THEN_BRANCH:
            case ELSE_BRANCH:
                visitTree();
                break;
        }
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int h){
        height=h;
    }

    public CSNode getParent() {
        return parent;
    }

    public CSType getCsType() {
        return csType;
    }

    public ArrayList<CSNode> getAncestorFW() {
        return ancestorFW;
    }

    public ArrayList<CSNode> getChildren() {
        return children;
    }

    public void setParent(CSNode parent) {
        this.parent = parent;
    }

    public Node getTreeNode() {
        return treeNode;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public int getTIndex() {
        return tIndex;
    }

    public ArrayList<CSNode> getAncestors() {
        return ancestors;
    }

    public boolean isNew() {
        return isNew;
    }

    public void updateChildrenHeight(){
        for (CSNode child:children) {
            child.height = this.height +1;
            child.updateChildrenHeight();
        }
    }

    public void setChildren(ArrayList<CSNode> children) {
        this.children = children;
    }

    public static int getCount (CSNode node, int sum){
        sum = sum +1;
        for(CSNode child: node.getChildren()){
            sum = sum+getCount(child,0);
        }
        return sum;
    }
}
