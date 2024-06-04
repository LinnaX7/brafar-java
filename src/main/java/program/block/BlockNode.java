package program.block;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.Node;
import variables.Variable;

public class BlockNode {
    private BlockType blockType;
    private boolean isEmpty;
    private final List<String> lineCodes;
    private final List<String> lineNumbers;
    private final List<Node> treeNodes;
    private JumpBlock jumpBlock;
    private BlockNode parentBlock;
    private final ArrayList<BlockNode> childBlocks;
    private int treeIndex;
    private Node parentNode;
    private final ArrayList<Integer> breakPointInIndexes;
    private final ArrayList<Integer> breakPointIndexes;
    private int blockMark;
    private final Set<Variable> relatedVars;
    private int metaIndex;

    private ArrayList<Boolean> condValues = new ArrayList<>();

    public ArrayList<Boolean> getCondValues() {
        return condValues;
    }

    public void addCondValues(boolean condValue) {
        this.condValues.add(condValue);
    }


    public BlockNode()
    {
        lineCodes = new ArrayList<>();
        lineNumbers = new ArrayList<>();
        treeNodes =new ArrayList<>();
        breakPointIndexes = new ArrayList<>();
        breakPointInIndexes = new ArrayList<>();
        childBlocks = new ArrayList<>();
        isEmpty = false;
        jumpBlock = null;
        parentBlock = null;
        treeIndex = 0;
        parentNode = null;
        blockMark = 0;
        relatedVars = new LinkedHashSet<>();
        metaIndex = -1;
    }

    public Set<Variable> getRelatedVars() {
        return relatedVars;
    }

    public void setMetaIndex(int metaIndex) {
        this.metaIndex = metaIndex;
    }

    public int getMetaIndex() {
        return metaIndex;
    }

    public void addRelatedVar(Variable variable){
        relatedVars.add(variable);
    }

    public int getBlockMark() {
        return blockMark;
    }

    public void setBlockMark(int blockMark) {
        this.blockMark = blockMark;
    }

    public ArrayList<BlockNode> getChildBlocks() {
        return childBlocks;
    }

    public void setTreeIndex(int treeIndex) {
        this.treeIndex = treeIndex;
    }

    public int getTreeIndex() {
        return treeIndex;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public void setJumpBlock(JumpBlock jumpBlock) {
        this.jumpBlock = jumpBlock;
    }

    public JumpBlock getJumpBlock() {
        return jumpBlock;
    }

    public void setParentBlock(BlockNode parentBlock) {
        this.parentBlock = parentBlock;
    }

    public BlockNode getParentBlock() {
        return parentBlock;
    }


    public void addBreakPointIndexes(int breakPointIndex) {
        this.breakPointIndexes.add(breakPointIndex);
    }

    public void addBreakPointInIndexes(int breakPointIndex){
        this.breakPointInIndexes.add(breakPointIndex);
    }

    public ArrayList<Integer> getBreakPointInIndexes() {
        return breakPointInIndexes;
    }

    public ArrayList<Integer> getBreakPointIndexes() {
        return breakPointIndexes;
    }

    public boolean getEmpty(){
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }


    public List<String> getLineCodes()
    {
        return lineCodes;
    }


    public List<Node> getTreeNodes()
    {
        return this.treeNodes;
    }


    public void addLineCode(String line)
    {
        lineCodes.add(line);
    }

    public void addLineNumber(String lineNumber){
        lineNumbers.add(lineNumber);
    }

    public void addTreeNode(Node node){
        treeNodes.add(node);
    }

    public void setBlockType(BlockType blockType) {
        this.blockType = blockType;
    }

    public List<String> getLineNumbers() {
        return lineNumbers;
    }

    public String getLastLineNumber(){
        return getLineNumbers().get(getLineNumbers().size()-1);
    }

    public int getLastBreakPointIndex(){
        return breakPointIndexes.get(breakPointIndexes.size()-1);
    }

    public int getLastBreakPointInIndex(){
        return breakPointInIndexes.get(breakPointInIndexes.size()-1);
    }

    public void addAttributeFromNode(Node node){
        this.setEmpty(false);
        this.addTreeNode(node);
        this.addLineCode(node.toString());
        assert node.getBegin().isPresent();
        assert node.getEnd().isPresent();
        for (int i = node.getBegin().get().line; i <= node.getEnd().get().line; i++) {
            this.addLineNumber(String.valueOf(i));
        }

    }
}
