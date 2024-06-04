package program.block;

import com.github.javaparser.ast.Node;
import variables.Variable;

import java.util.LinkedHashSet;
import java.util.Set;

public class JumpBlock {
    private BlockType type;
    private String lineCode;
    private String lineNumber;
    private Node treeNode;
    private int treeIndex;
    private int blockMark;
    private final Set<Variable> relatedVars;
    private int metaIndex;

    public void setMetaIndex(int metaIndex) {
        this.metaIndex = metaIndex;
    }

    public int getMetaIndex() {
        return metaIndex;
    }

    public JumpBlock(){
        relatedVars = new LinkedHashSet<>();
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public void setTreeNode(Node treeNode) {
        this.treeNode = treeNode;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public BlockType getType() {
        return type;
    }

    public Node getTreeNode() {
        return treeNode;
    }
    public void addRelatedVar(Variable variable){
        relatedVars.add(variable);
    }

    public Set<Variable> getRelatedVars() {
        return relatedVars;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public int getTreeIndex() {
        return treeIndex;
    }

    public void setTreeIndex(int treeIndex) {
        this.treeIndex = treeIndex;
    }

    public void setBlockMark(int blockMark) {
        this.blockMark = blockMark;
    }

    public int getBlockMark() {
        return blockMark;
    }
}
