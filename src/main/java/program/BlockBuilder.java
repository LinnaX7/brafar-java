package program;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import program.block.BlockNode;
import program.block.BlockType;
import program.block.JumpBlock;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.*;
import variables.ValueIndex;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BlockBuilder {
    public static int RETURN_VALUE = 6;
    public static int LOOP_VALUE = 6;
    public static int IF_COND_VALUE = 4;
    public static int BASIC_BLOCK_VALUE = 1;
    ArrayList<BlockNode> blockNodes;

    public ArrayList<BlockNode> getBlockNodes() {
        return blockNodes;
    }

    public BlockBuilder(MethodDeclaration methodDeclaration){
        blockNodes = new ArrayList<>();
        assert methodDeclaration.getBody().isPresent();
        visit(methodDeclaration.getBody().get(), null);
    }

    public void clearBlockBPL(BlockNode blockNode){
        blockNode.getBreakPointIndexes().clear();
        blockNode.getBreakPointInIndexes().clear();
        for(BlockNode childNode:blockNode.getChildBlocks()){
            clearBlockBPL(childNode);
        }
    }

    public void clearBlockBPL(){
        for(BlockNode blockNode: blockNodes){
            clearBlockBPL(blockNode);
        }
    }

    private void visit(BlockStmt blockStmt, BlockNode parentBlock){
        int tIndex = 0;
        addEmptyBlock(parentBlock, blockStmt, 0);
        for(Statement statement:blockStmt.getStatements()){
            visitStatement(statement, parentBlock, blockStmt, tIndex);
            tIndex += 1;
        }
    }

    private void visit(BlockNode parentBlock, ExpressionStmt expression){
        BlockNode blk = getLastChildBlock(parentBlock);
        blk.setBlockType(BlockType.BASIC_BLOCK);
        blk.setEmpty(false);
        blk.setBlockMark(BASIC_BLOCK_VALUE);
        blk.addAttributeFromNode(expression);
    }

    private BlockNode addNewBlock(Node node, BlockType type, BlockNode parentBlock, Node parent, int tIndex){
        BlockNode blockNode = new BlockNode();
        blockNode.setBlockType(type);
        blockNode.addAttributeFromNode(node);
        blockNode.setParentBlock(parentBlock);
        blockNode.setParentNode(parent);
        blockNode.setTreeIndex(tIndex);
        addParentChild(parentBlock, blockNode);
        return blockNode;
    }


    private void addParentChild(BlockNode parentBlock, BlockNode blockNode){
        if(!(parentBlock==null)) {
            parentBlock.getChildBlocks().add(blockNode);
            return;
        }
        getBlockNodes().add(blockNode);
    }

    private void addEmptyBlock(BlockNode parentBlock, Node parent, int tIndex){
        BlockNode blockNode = new BlockNode();
//        BlockNodes.add(basicBlock);
        addParentChild(parentBlock, blockNode);
        blockNode.setEmpty(true);
        blockNode.setBlockType(BlockType.EMPTY_BLOCK);
        blockNode.setParentNode(parent);
        blockNode.setTreeIndex(tIndex);
        blockNode.setParentBlock(parentBlock);
    }

    private void visit(IfStmt ifStmt, BlockNode parentBlock, Node parent, int tIndex){
        BlockNode ifBlock = addNewBlock(ifStmt, BlockType.IF_BLOCK, parentBlock, parent, tIndex);
        Expression cond = ifStmt.getCondition();
        BlockNode ifCond = addNewBlock(cond, BlockType.IF_COND, ifBlock, ifStmt, 0);
        ifCond.setBlockMark(IF_COND_VALUE);
        BlockNode ifBody = addNewBlock(ifStmt.getThenStmt(), BlockType.IF_BODY, ifBlock, ifStmt.getThenStmt(), 1);
        visit((BlockStmt) ifStmt.getThenStmt(), ifBody);
        if(ifStmt.getElseStmt().isPresent()){
            BlockNode elseBody = addNewBlock(ifStmt.getElseStmt().get(), BlockType.ELSE_BODY, ifBlock, ifStmt.getElseStmt().get(), 2);
            visit((BlockStmt) ifStmt.getElseStmt().get(), elseBody);
        }
        addEmptyBlock(parentBlock, parent, tIndex + 1);
    }

    private void visit(WhileStmt whileStmt, BlockNode parentBlock, Node parent, int tIndex){
        BlockNode whileBlock = addNewBlock(whileStmt, BlockType.WHILE_BLOCK, parentBlock, parent, tIndex);
        Expression cond = whileStmt.getCondition();
        BlockNode whileCond = addNewBlock(cond, BlockType.WHILE_COND, whileBlock, whileStmt, 0);
        whileCond.setBlockMark(LOOP_VALUE);
        BlockNode whileBody = addNewBlock(whileStmt.getBody(), BlockType.WHILE_BODY, whileBlock, whileStmt.getBody(), 1);
        visit((BlockStmt) whileStmt.getBody(), whileBody);
        addEmptyBlock(parentBlock, parent, tIndex +1 );
    }

    private void visit(ForStmt forStmt, BlockNode parentBlock, Node parent, int tIndex){
        BlockNode forBlock = addNewBlock(forStmt, BlockType.FOR_BLOCK, parentBlock, parent, tIndex);
        BlockNode blk = new BlockNode();
        blk.setBlockType(BlockType.FOR_INIT);
        blk.setParentNode(forStmt);
        blk.setParentBlock(forBlock);
        blk.setTreeIndex(tIndex);
        for(Expression init: forStmt.getInitialization()) {
            blk.addAttributeFromNode(init);
        }
        blk.setBlockMark(LOOP_VALUE);
        addParentChild(forBlock, blk);
        BlockNode blk1 = new BlockNode();
        blk1.setBlockType(BlockType.FOR_COMP);
        blk1.setParentNode(forStmt);
        blk1.setParentBlock(forBlock);
        blk1.setTreeIndex(tIndex);
        if(forStmt.getCompare().isPresent()){
            blk1.addAttributeFromNode(forStmt.getCompare().get());
        }
        blk1.setBlockMark(LOOP_VALUE);
        addParentChild(forBlock, blk1);
        BlockNode blk2 = new BlockNode();
        blk2.setBlockType(BlockType.FOR_UPDATE);
        blk2.setParentNode(forStmt);
        blk2.setParentBlock(forBlock);
        blk2.setTreeIndex(tIndex);
        for(Expression update:forStmt.getUpdate()){
            blk2.addAttributeFromNode(update);
        }
        blk2.setBlockMark(LOOP_VALUE);
        addParentChild(forBlock, blk2);
        BlockNode forBody = addNewBlock(forStmt.getBody(), BlockType.FOR_BODY, forBlock, forStmt.getBody(), 3);
        visit((BlockStmt) forStmt.getBody(), forBody);
        addEmptyBlock(parentBlock, parent, tIndex+1);
    }

    private void visit(ForEachStmt forEachStmt, BlockNode parentBlock, Node parent, int tIndex){
        BlockNode forEachBlock = addNewBlock(forEachStmt, BlockType.FOREACH_BLOCK, parentBlock, parent, tIndex);
        BlockNode blk = new BlockNode();
        blk.setBlockType(BlockType.FOREACH_ITER);
        blk.setParentNode(forEachStmt);
        blk.setParentBlock(forEachBlock);
        blk.setTreeIndex(tIndex);
        blk.addLineCode(forEachStmt.getVariable().toString());
        blk.addLineCode(forEachStmt.getIterable().toString());
        blk.addTreeNode(forEachStmt.getVariable());
        blk.addTreeNode(forEachStmt.getIterable());
        assert forEachStmt.getVariable().getEnd().isPresent();
        blk.addLineNumber(String.valueOf(forEachStmt.getVariable().getEnd().get().line));
        blk.setBlockMark(LOOP_VALUE);
        addParentChild(forEachBlock, blk);
        BlockNode forEachBody = addNewBlock(forEachStmt.getBody(), BlockType.FOREACH_BODY, forEachBlock, forEachStmt.getBody(), 0);
        visit((BlockStmt) forEachStmt.getBody(), forEachBody);
        addEmptyBlock(parentBlock, parent, tIndex+1);
    }

    private void visit(SwitchStmt switchStmt, BlockNode parentBlock, Node parent, int tIndex){
        BlockNode switchBlock = addNewBlock(switchStmt, BlockType.SWITCH_BLOCK, parentBlock, parent, tIndex);
        BlockNode blk = addNewBlock(switchStmt.getSelector(), BlockType.SWITCH_SELECTOR, switchBlock, switchStmt, 0);
        int i = 0;
        for(SwitchEntry switchEntry:switchStmt.getEntries()){
            BlockNode switchEntryBlock = addNewBlock(switchEntry, BlockType.SWITCH_ENTRY, switchBlock, switchStmt, 1);
            BlockNode blk1 = new BlockNode();
            blk1.setBlockType(BlockType.CASE_LABELS);
            blk1.setParentNode(switchStmt);
            blk1.setParentBlock(blk);
            blk1.setTreeIndex(i);
            for(Expression expression:switchEntry.getLabels()) {
                blk1.addAttributeFromNode(expression);
            }
            addParentChild(switchEntryBlock, blk1);
            BlockNode caseBody = addNewBlock(switchEntry, BlockType.CASE_BODY, switchEntryBlock, switchEntry, 1);
            addEmptyBlock(caseBody, switchEntry,0);
            int j = 0;
            for(Statement statement:switchEntry.getStatements()){
                visitStatement(statement, caseBody, switchEntry, j);
                j += 1;
            }
            i += 1;
        }
        addEmptyBlock(parentBlock, parent, tIndex+1);
    }

    public BlockNode getLastChildBlock(BlockNode parentBlock){
        BlockNode blk;
        if(parentBlock == null) {
            blk = blockNodes.get(blockNodes.size() - 1);
        }else{
            blk = parentBlock.getChildBlocks().get(parentBlock.getChildBlocks().size()-1);
        }
        return blk;
    }

    private void addJumpNode(BlockNode parentBlock, Statement statement, BlockType blockType, int tIndex){
        BlockNode blk = getLastChildBlock(parentBlock);
        JumpBlock jumpBlock = new JumpBlock();
        jumpBlock.setTreeNode(statement);
        jumpBlock.setLineCode(statement.toString());
        assert statement.getEnd().isPresent();
        jumpBlock.setLineNumber(String.valueOf(statement.getEnd().get().line));
        jumpBlock.setType(blockType);
        jumpBlock.setTreeIndex(tIndex);
        blk.setJumpBlock(jumpBlock);
    }

    private void visit(BlockNode parentBlock, ContinueStmt continueStmt, int tIndex){
        addJumpNode(parentBlock, continueStmt, BlockType.CONTINUE, tIndex);
    }

    private void visit(BlockNode parentBlock, BreakStmt breakStmt, int tIndex){
        addJumpNode(parentBlock, breakStmt, BlockType.BREAK, tIndex);
    }

    private void visit(BlockNode parentBlock, ReturnStmt returnStmt, int tIndex){
        addJumpNode(parentBlock, returnStmt, BlockType.RETURN, tIndex);
        BlockNode blk = getLastChildBlock(parentBlock);
        blk.getJumpBlock().setBlockMark(RETURN_VALUE);
    }

    public void visit(BlockNode parentBlock, EmptyStmt emptyStmt, int tIndex){
        addJumpNode(parentBlock, emptyStmt, BlockType.EMPTY_STMT,tIndex);
    }

    private void visitStatement(Statement statement, BlockNode parentBlock, Node parent, int tIndex){
        if(statement instanceof BlockStmt){
            visit((BlockStmt) statement, parentBlock);
        }else if(statement instanceof IfStmt) {
            visit((IfStmt) statement, parentBlock, parent, tIndex);
        } else if(statement instanceof SwitchStmt){
            visit((SwitchStmt) statement, parentBlock, parent, tIndex);
        }else if(statement instanceof ForStmt){
            visit((ForStmt) statement, parentBlock,parent, tIndex);
        }else if(statement instanceof WhileStmt){
            visit((WhileStmt) statement,parentBlock, parent, tIndex);
        }else if(statement instanceof ForEachStmt){
            visit((ForEachStmt) statement,parentBlock,parent, tIndex);
        }else if(statement instanceof ContinueStmt){
            visit(parentBlock,(ContinueStmt) statement,tIndex);
        }else if(statement instanceof BreakStmt){
            visit(parentBlock, (BreakStmt) statement,tIndex);
        }else if(statement instanceof ReturnStmt){
            visit(parentBlock, (ReturnStmt) statement, tIndex);
        }else if(statement instanceof  ExpressionStmt){
            visit(parentBlock, (ExpressionStmt) statement);
        }else if(statement instanceof EmptyStmt){
            visit(parentBlock, (EmptyStmt) statement, tIndex);
        }
    }

    public static ArrayList<Integer> getLoopIndexes(BlockNode blockNode, StackBuffer stackBuffer){
//        ArrayList<Integer> loopIns = new ArrayList<>();
        if(stackBuffer.getOriginalIndex(blockNode.getLineNumbers().get(0))!=-1){
            return stackBuffer.getAllIndexes(blockNode.getChildBlocks().get(1).getLineNumbers().get(0));
        }
        return new ArrayList<>();
    }

    public static boolean getCondFlag(BlockNode condBlock, BlockNode bodyNode, StackBuffer stackBuffer){

        Node cond = condBlock.getTreeNodes().get(0);
        if(cond instanceof BooleanLiteralExpr)
            return ((BooleanLiteralExpr) cond).getValue();
        return getBodyFlag(bodyNode, stackBuffer);
    }

    public static boolean getBodyFlag(BlockNode bodyNode, StackBuffer stackBuffer){
        int i = 0;
        if(bodyNode.getLineCodes().get(0).split("\n")[0].replace("\r","").equals("{")){
            i = 1;
        }
        for (String line : bodyNode.getLineNumbers().subList(i,bodyNode.getLineNumbers().size())) {

            if (stackBuffer.getOriginalIndex(line) != -1) {
                return true;
            }
        }
        return false;
    }

    private void setIfBPL(BlockNode ifBlock, StackBuffer stackBuffer){
        BlockNode ifCond = ifBlock.getChildBlocks().get(0);
        BlockNode ifBody = ifBlock.getChildBlocks().get(1);

        BlockNode elseBody = null;

        if(ifBlock.getChildBlocks().size()==3){
            elseBody = ifBlock.getChildBlocks().get(2);
        }
        boolean condFlag = getCondFlag(ifCond, ifBody, stackBuffer);
//        boolean ifBodyFlag = getBodyFlag(ifBody, stackBuffer);
        ifCond.addCondValues(condFlag);
        setBodyBPL(ifBody.getChildBlocks(), stackBuffer);
        if(elseBody!=null)
            setBodyBPL(elseBody.getChildBlocks(),stackBuffer);
    }

    private void setForBPL(BlockNode forBlock, StackBuffer stackBuffer){
        BlockNode forInit = forBlock.getChildBlocks().get(0);
        BlockNode forComp = forBlock.getChildBlocks().get(1);
        BlockNode forUpdate = forBlock.getChildBlocks().get(2);
        BlockNode forBody = forBlock.getChildBlocks().get(3);
        forInit.addBreakPointInIndexes(stackBuffer.beginIndex);
        ArrayList<Integer> loopBlockIndexes = getLoopIndexes(forBlock, stackBuffer);
        for (int i = 0; i < loopBlockIndexes.size(); i++) {
            int beginIndex = loopBlockIndexes.get(i)+1;
            int endIndex = stackBuffer.endIndex;
            StackBuffer bodyStackBuffer = new StackBuffer(beginIndex, endIndex, stackBuffer.breakPointLineIndexes);
            if(i < loopBlockIndexes.size()-1) {
                bodyStackBuffer.setEndIndex(loopBlockIndexes.get(i + 1));
                bodyStackBuffer.setOutIndex(loopBlockIndexes.get(i + 1));
            }else{
                bodyStackBuffer.setOutIndex(stackBuffer.outIndex);
            }
            int index = getBlockValueBeginIndex(forBody, bodyStackBuffer);
            if(i==0){
                forInit.addBreakPointIndexes(index);
            }else{
                forUpdate.addBreakPointInIndexes(loopBlockIndexes.get(i));
                forUpdate.addBreakPointIndexes(index);
            }
            if(index != -1)
                forComp.addCondValues(true);
            else
                forComp.addCondValues(false);
            setBodyBPL(forBody.getChildBlocks(), bodyStackBuffer);
        }
        if(loopBlockIndexes.size()==0){
            forInit.addBreakPointInIndexes(stackBuffer.endIndex);
            forComp.addCondValues(false);
        }
    }

    private void setForEachBPL(BlockNode loopBlock, StackBuffer stackBuffer){
        BlockNode loopCond = loopBlock.getChildBlocks().get(0);
        BlockNode loopBody = loopBlock.getChildBlocks().get(1);
        int index = getBlockValueBeginIndex(loopBody, stackBuffer);
        if(index == -1)
            loopCond.addCondValues(false);
        else
            loopCond.addCondValues(true);
        ArrayList<Integer> loopBlockIndexes = getLoopIndexes(loopBlock, stackBuffer);
        for (int i = 0; i < loopBlockIndexes.size(); i++) {
            int beginIndex = loopBlockIndexes.get(i)+1;
            int endIndex = stackBuffer.endIndex;
            StackBuffer bodyStackBuffer = new StackBuffer(beginIndex, endIndex, stackBuffer.breakPointLineIndexes);
            if(i < loopBlockIndexes.size()-1) {
                bodyStackBuffer.setEndIndex(loopBlockIndexes.get(i + 1));
                bodyStackBuffer.setOutIndex(loopBlockIndexes.get(i + 1));
            }else{
                bodyStackBuffer.setOutIndex(stackBuffer.outIndex);
            }
            index = getBlockValueBeginIndex(loopBody, bodyStackBuffer);
            if(index != -1)
                loopCond.addCondValues(true);
            else
                loopCond.addCondValues(false);
            setBodyBPL(loopBody.getChildBlocks(), bodyStackBuffer);
        }

    }

    private void setWhileBPL(BlockNode whileBlock, StackBuffer stackBuffer){
        BlockNode loopCond = whileBlock.getChildBlocks().get(0);
        BlockNode loopBody = whileBlock.getChildBlocks().get(1);
        boolean condFlag = getCondFlag(loopCond, loopBody, stackBuffer);
//        boolean ifBodyFlag = getBodyFlag(ifBody, stackBuffer);
        loopCond.addCondValues(condFlag);
        ArrayList<Integer> loopBlockIndexes = getLoopIndexes(whileBlock, stackBuffer);
        for (int i = 0; i < loopBlockIndexes.size(); i++) {
            int beginIndex = loopBlockIndexes.get(i)+1;
            int endIndex = stackBuffer.endIndex;
            StackBuffer bodyStackBuffer = new StackBuffer(beginIndex, endIndex, stackBuffer.breakPointLineIndexes);
            if(i < loopBlockIndexes.size()-1) {
                bodyStackBuffer.setEndIndex(loopBlockIndexes.get(i + 1));
                bodyStackBuffer.setOutIndex(loopBlockIndexes.get(i + 1));
            }else{
                bodyStackBuffer.setOutIndex(stackBuffer.outIndex);
            }
            condFlag = getCondFlag(loopCond, loopBody, bodyStackBuffer);
//        boolean ifBodyFlag = getBodyFlag(ifBody, stackBuffer);
            loopCond.addCondValues(condFlag);
            setBodyBPL(loopBody.getChildBlocks(), bodyStackBuffer);
        }


    }



    private void setBodyBPL(ArrayList<BlockNode> blockChildren, StackBuffer stackBuffer){
        ArrayList<ValueIndex> blockNodesValueIndexes = getChildrenIndexes(blockChildren, stackBuffer);
        for (int i = 0; i < blockChildren.size(); i++) {
            BlockNode currentBlock = blockChildren.get(i);
            int inValueIndex = blockNodesValueIndexes.get(i).inValueIndex;
            int endIndex;
            if(blockNodesValueIndexes.get(i).outValueIndex != -1)
                endIndex = blockNodesValueIndexes.get(i).outValueIndex;
            else endIndex = stackBuffer.endIndex;
            if(inValueIndex == -1)
                inValueIndex = endIndex;
            StackBuffer childStackBuffer = new StackBuffer(inValueIndex, endIndex, stackBuffer.breakPointLineIndexes);
            switch (currentBlock.getBlockType()){

                case BASIC_BLOCK:
                case EMPTY_BLOCK:
                    currentBlock.addBreakPointInIndexes(inValueIndex);
                    currentBlock.addBreakPointIndexes(endIndex);
                    break;
                case IF_BLOCK:
                    setIfBPL(currentBlock, childStackBuffer);
                    break;
                case FOR_BLOCK:
                    setForBPL(currentBlock, childStackBuffer);
                    break;
                case FOREACH_BLOCK:
                    setForEachBPL(currentBlock, childStackBuffer);
                    break;
                case WHILE_BLOCK:
                    setWhileBPL(currentBlock, childStackBuffer);
                    break;
            }
        }
    }

    public static int getBlockValueBeginIndex(BlockNode blockNode, StackBuffer stackBuffer) {
        int index = -1;
        switch (blockNode.getBlockType()){
            case BASIC_BLOCK:
            case IF_BLOCK:
            case FOR_BLOCK:
            case WHILE_BLOCK:
            case FOREACH_BLOCK:
            case FOR_BODY:
            case ELSE_BODY:
            case IF_BODY:
            case IF_COND:
            case RETURN:
            case BREAK:
            case CONTINUE:
            case FOR_UPDATE:
            case FOREACH_BODY:
            case WHILE_BODY:
            case WHILE_COND:
            case FOR_COMP:
            case FOR_INIT:
            case FOREACH_ITER:
                for(String line:blockNode.getLineNumbers()){
                    index = stackBuffer.getOriginalIndex(line);
                    if(index!=-1)
                        break;
                }
                return index;
            case EMPTY_BLOCK:
                index = getEmptyBlockIndex(blockNode, stackBuffer);
                return index;
        }
        return index;
    }

    public static int getEmptyBlockIndex(BlockNode blockNode, StackBuffer stackBuffer){
        if(blockNode.getJumpBlock()!=null &&blockNode.getJumpBlock().getType()==BlockType.RETURN) {
            return getJumpBlockIndex(blockNode.getJumpBlock(), stackBuffer);
        }
        return -1;
    }

    public static int getJumpBlockIndex(JumpBlock jumpBlock, StackBuffer stackBuffer) {
        int index;
        index = stackBuffer.getOriginalIndex(jumpBlock.getLineNumber());
        return index;
    }

    public static ArrayList<ValueIndex> getChildrenIndexes(ArrayList<BlockNode> children, StackBuffer stackBuffer){
        ArrayList<ValueIndex> blockIndexes = new ArrayList<>();
        for (BlockNode child : children) {
            ValueIndex blockValueIndex = new ValueIndex(-1, -1);
            blockValueIndex.setInValueIndex(getBlockValueBeginIndex(child, stackBuffer));
            blockIndexes.add(blockValueIndex);
        }
        BlockNode lastChild = children.get(children.size()-1);
        if(lastChild.getBlockType()==BlockType.BASIC_BLOCK||lastChild.getBlockType()==BlockType.EMPTY_BLOCK){
            if(lastChild.getJumpBlock()!=null && lastChild.getJumpBlock().getType()==BlockType.RETURN){
                blockIndexes.get(blockIndexes.size()-1).setOutValueIndex(getJumpBlockIndex(lastChild.getJumpBlock(),stackBuffer));
            }else{
                blockIndexes.get(blockIndexes.size()-1).setOutValueIndex(stackBuffer.outIndex);
            }
        }else{
            blockIndexes.get(blockIndexes.size()-1).setOutValueIndex(stackBuffer.outIndex);
        }
        if(lastChild.getBlockType()==BlockType.EMPTY_BLOCK && blockIndexes.get(blockIndexes.size()-1).getInValueIndex()==-1)
            blockIndexes.get(blockIndexes.size()-1).setInValueIndex(stackBuffer.outIndex);
        for (int i = blockIndexes.size()-2; i >=0 ; i--) {
            if(blockIndexes.get(i).getInValueIndex()==-1)
                blockIndexes.get(i).setInValueIndex(blockIndexes.get(i+1).getInValueIndex());
            blockIndexes.get(i).setOutValueIndex(blockIndexes.get(i+1).getInValueIndex());
        }
        return blockIndexes;
    }
    public void setBodyChildrenBPL(@Nullable BlockNode bodyBlock, int beginIndex, int endIndex, ArrayList<String> originalBreakPointLines){
        ArrayList<BlockNode> children;
        if(bodyBlock == null)
            children = getBlockNodes();
        else
            children = bodyBlock.getChildBlocks();
        setBodyBPL(children, new StackBuffer(beginIndex, endIndex, originalBreakPointLines));
    }



//    public void setBodyChildrenBPL(@Nullable BlockNode bodyBlock, int beginIndex, int endIndex, ArrayList<String> originalBreakPointLines){
//        ArrayList<BlockNode> children;
//        if(bodyBlock == null)
//            children = getBlockNodes();
//        else
//            children = bodyBlock.getChildBlocks();
//
//        for (BlockNode child : children) {
//            switch (child.getBlockType()) {
//                case BASIC_BLOCK:
//                    setBasicBlockBPL(child, beginIndex, endIndex, originalBreakPointLines);
//                case EMPTY_BLOCK:
//                    break;
//                case IF_BLOCK:
//                    setIfBlockBPL(child,  beginIndex,endIndex, originalBreakPointLines);
//                    break;
//                case FOR_BLOCK:
//                    setForBlockBPL(child,  beginIndex, endIndex, originalBreakPointLines);
//                    break;
//                case FOREACH_BLOCK:
//                case WHILE_BLOCK:
//                    setWhileForEachBlockBPL(child, beginIndex, endIndex, originalBreakPointLines);
//                    break;
//            }
//        }
//
//        for (int i = 0; i < children.size(); i++) {
//            BlockNode child = children.get(i);
//            if(child.getBlockType()==BlockType.EMPTY_BLOCK){
//                if(child.getJumpBlock()!=null){
//                    if(child.getJumpBlock().getType()!=BlockType.EMPTY_STMT){
//                        String lineNumber = child.getJumpBlock().getLineNumber();
//                        int index = originalBreakPointLines.subList(beginIndex,endIndex).indexOf(lineNumber);
//                        if(index !=-1) {
//                            child.addBreakPointInIndexes(beginIndex +index);
//                            child.addBreakPointIndexes(beginIndex +index);
//                        }
//                        continue;
//                    }
//                }
//                if(i==0){
//                    if(children.size()==1) {
//                        if (bodyBlock != null) {
//                            if(bodyBlock.getBreakPointInIndexes().size()!=0) {
//                                child.addBreakPointInIndexes(bodyBlock.getLastBreakPointInIndex());
//                                child.addBreakPointIndexes(bodyBlock.getLastBreakPointInIndex());
//                            }
//                        }
//                    }else {
//                        if (children.get(i + 1).getBreakPointInIndexes().size() != 0) {
//                            child.addBreakPointIndexes(children.get(i + 1).getLastBreakPointInIndex());
//                            child.addBreakPointInIndexes(children.get(i + 1).getLastBreakPointInIndex());
//                        }
//                    }
//                }else{
//                    if (children.get(i - 1).getBreakPointInIndexes().size() != 0) {
//                        child.addBreakPointIndexes(children.get(i - 1).getLastBreakPointIndex());
//                        child.addBreakPointInIndexes(children.get(i - 1).getLastBreakPointIndex());
//                    }
//                }
//            }
//        }
//        BlockNode lastChildren = children.get(children.size()-1);
//        if(bodyBlock!=null) {
//            if(lastChildren.getBreakPointIndexes().size()!=0)
//                bodyBlock.addBreakPointIndexes(lastChildren.getLastBreakPointInIndex());
//            else
//                bodyBlock.addBreakPointIndexes(-1);
//        }
//    }
//    public void setIfBlockBPL(BlockNode ifBlock, int beginIndex, int endIndex, ArrayList<String> originalBreakPointLines){
//        BlockNode ifCond = ifBlock.getChildBlocks().get(0);
//        BlockNode ifBody = ifBlock.getChildBlocks().get(1);
//        BlockNode elseBody = null;
//        if(ifBlock.getChildBlocks().size()==3){
//            elseBody = ifBlock.getChildBlocks().get(2);
//        }
//        int index = originalBreakPointLines.subList(beginIndex,endIndex).indexOf(ifCond.getLastLineNumber());
//        if(index == -1)return;
//        ifBlock.addBreakPointInIndexes(beginIndex+index);
//        ifCond.addBreakPointInIndexes(beginIndex + index);
//        if(index+1<originalBreakPointLines.subList(beginIndex, endIndex).size()){
//            ifCond.addBreakPointIndexes(beginIndex + index+1);
//            if(Integer.parseInt(originalBreakPointLines.subList(beginIndex, endIndex).get(index+1))>Integer.parseInt(ifBody.getLastLineNumber())){
//                ifBody.addBreakPointInIndexes(-1);
//                ifBody.addBreakPointIndexes(-1);
//                if(elseBody!=null){
//                    if(Integer.parseInt(originalBreakPointLines.subList(beginIndex, endIndex).get(index+1))<Integer.parseInt(elseBody.getLastLineNumber())) {
//                        elseBody.addBreakPointInIndexes(beginIndex+index + 1);
//                        setBodyChildrenBPL(elseBody, beginIndex+index, endIndex, originalBreakPointLines);
//                    }else{
//                        elseBody.addBreakPointInIndexes(beginIndex + index);
//                        elseBody.addBreakPointIndexes(beginIndex + index);
//                    }
//                    ifBlock.addBreakPointIndexes(elseBody.getBreakPointIndexes().get(elseBody.getBreakPointIndexes().size()-1));
//                }else{
//                    ifBlock.addBreakPointIndexes(beginIndex+index);
//                }
//            }else{
//                ifBody.addBreakPointInIndexes(beginIndex+index+1);
//                setBodyChildrenBPL(ifBody,beginIndex+index, endIndex, originalBreakPointLines);
//                ifBlock.addBreakPointIndexes(ifBody.getBreakPointIndexes().get(ifBody.getBreakPointIndexes().size()-1));
//                if(elseBody!=null){
//                    elseBody.addBreakPointInIndexes(-1);
//                    elseBody.addBreakPointIndexes(-1);
//                }
//            }
//        }else{
//            ifCond.addBreakPointIndexes(beginIndex+index);
//            ifBlock.addBreakPointIndexes(beginIndex + index);
//            ifBody.addBreakPointInIndexes(-1);
//            ifBody.addBreakPointIndexes(-1);
//            if(elseBody!=null){
//                elseBody.addBreakPointInIndexes(-1);
//                elseBody.addBreakPointIndexes(-1);
//            }
//        }
//    }
//
//    public void setForBlockBPL(BlockNode forBlock, int beginIndex, int endIndex, ArrayList<String> originalBreakPointLines){
//        BlockNode forInit = forBlock.getChildBlocks().get(0);
//        BlockNode forComp = forBlock.getChildBlocks().get(1);
//        BlockNode forUpdate = forBlock.getChildBlocks().get(2);
//        BlockNode forBody = forBlock.getChildBlocks().get(3);
//
//        int index = originalBreakPointLines.subList(beginIndex,endIndex).indexOf(forInit.getLastLineNumber());
//        if(index == -1)return;
//        forBlock.addBreakPointInIndexes(beginIndex + index);
//        forInit.addBreakPointInIndexes(beginIndex+index);
//        if(index+1<originalBreakPointLines.subList(beginIndex,endIndex).size()){
//            if(Integer.parseInt(originalBreakPointLines.subList(beginIndex,endIndex).get(index+1))>Integer.parseInt(forBody.getLastLineNumber())){
//                forBody.addBreakPointInIndexes(-1);
//                forBody.addBreakPointIndexes(-1);
//                forInit.addBreakPointIndexes(beginIndex+index);
//                forBlock.addBreakPointIndexes(beginIndex+index);
//            }else {
//                forInit.addBreakPointIndexes(beginIndex+index+1);
////                index = index + 1;
//                String loopLine = originalBreakPointLines.subList(beginIndex,endIndex).get(index);
//                int lastIndex = originalBreakPointLines.subList(beginIndex,endIndex).lastIndexOf(loopLine);
//                while(index!=lastIndex){
//                    forComp.addBreakPointInIndexes(beginIndex + index +1);
//                    forComp.addBreakPointIndexes(beginIndex + index+1);
//                    int nextIndex = originalBreakPointLines.subList(beginIndex,endIndex).subList(index + 1, originalBreakPointLines.subList(beginIndex,endIndex).size()).indexOf(loopLine);
//                    forBody.addBreakPointInIndexes(beginIndex+index+1);
//                    setBodyChildrenBPL(forBody, beginIndex+index+1, beginIndex+index+1+nextIndex,originalBreakPointLines);
//                    forUpdate.addBreakPointInIndexes(beginIndex+index+nextIndex+1);
//                    forUpdate.addBreakPointIndexes(beginIndex+index+nextIndex+1+1);
//                    index = index+nextIndex+1;
//                }
//                if(beginIndex+index + 1<originalBreakPointLines.size()) {
//                    forBlock.addBreakPointIndexes(beginIndex+index+1);
//                }else{
//                    forBlock.addBreakPointIndexes(beginIndex+index);
//                }
//            }
//        }else{
//            forBlock.addBreakPointIndexes(beginIndex+index);
//            forInit.addBreakPointIndexes(-1);
//        }
//    }
//
//    public void setWhileForEachBlockBPL(BlockNode whileForEachBlock, int beginIndex, int endIndex, ArrayList<String> originalBreakPointLines){
//        BlockNode whileForEachIter = whileForEachBlock.getChildBlocks().get(0);
//        BlockNode whileForEachBody = whileForEachBlock.getChildBlocks().get(1);
//        int index = originalBreakPointLines.subList(beginIndex,endIndex).indexOf(whileForEachIter.getLastLineNumber());
//        if(index == -1)return;
//        whileForEachBlock.addBreakPointInIndexes(beginIndex + index);
//        whileForEachIter.addBreakPointInIndexes(beginIndex+index);
//        if(index+1<originalBreakPointLines.subList(beginIndex,endIndex).size()){
//            if(Integer.parseInt(originalBreakPointLines.subList(beginIndex,endIndex).get(index+1))>Integer.parseInt(whileForEachBody.getLastLineNumber())){
//                whileForEachBody.addBreakPointInIndexes(-1);
//                whileForEachBody.addBreakPointIndexes(-1);
//                whileForEachIter.addBreakPointIndexes(-1);
//                whileForEachBlock.addBreakPointIndexes(beginIndex+index+1);
//            }else{
//                whileForEachIter.addBreakPointIndexes(beginIndex+index+1);
//                String loopLine = originalBreakPointLines.subList(beginIndex,endIndex).get(index);
//                int lastIndex = originalBreakPointLines.subList(beginIndex,endIndex).lastIndexOf(loopLine);
//                while(index!=lastIndex){
//                    whileForEachBody.addBreakPointInIndexes(beginIndex+index+1);
//                    int nextIndex = originalBreakPointLines.subList(beginIndex,endIndex).subList(index + 1, originalBreakPointLines.subList(beginIndex,endIndex).size()).indexOf(loopLine);
//                    setBodyChildrenBPL(whileForEachBody, beginIndex+index+1, beginIndex+index+1+nextIndex,originalBreakPointLines);
//                    index = index+nextIndex+1;
//                    if(index!=lastIndex) {
//                        whileForEachIter.addBreakPointInIndexes(beginIndex + index);
//                        whileForEachIter.addBreakPointIndexes(beginIndex + index + 1);
//                    }
//                }
//                if(beginIndex + index + 1<originalBreakPointLines.size()) {
//                    whileForEachBlock.addBreakPointIndexes(beginIndex+index+1);
//                }else{
//                    whileForEachBlock.addBreakPointIndexes(beginIndex+index);
//                }
//            }
//        }else{
//            whileForEachBody.addBreakPointInIndexes(-1);
//            whileForEachBody.addBreakPointIndexes(-1);
//            whileForEachBlock.addBreakPointIndexes(beginIndex + index);
//            whileForEachIter.addBreakPointIndexes(beginIndex+index);
//        }
//    }
//
//    public void setBasicBlockBPL(BlockNode blockNode, int beginIndex, int endIndex, ArrayList<String> originalBreakPointLines){
//        int index = -1;
//        for (String line: blockNode.getLineNumbers()) {
//            index = originalBreakPointLines.subList(beginIndex,endIndex).indexOf(line);
//            if(index!=-1)break;
//        }
//        if(index == -1){
//            blockNode.addBreakPointInIndexes(-1);
//            blockNode.addBreakPointIndexes(-1);
//            return;
//        }
//        blockNode.addBreakPointInIndexes(index+beginIndex);
//        String lastLine = blockNode.getLastLineNumber();
//        int lastIndex = index;
//        for (String line: blockNode.getLineNumbers()) {
//            lastIndex = index;
//            index = originalBreakPointLines.subList(beginIndex,endIndex).indexOf(line);
//            if(index==-1)break;
//        }
//        if(index != -1)
//            lastIndex = index;
//        if(beginIndex+lastIndex+1<originalBreakPointLines.size()){
//            blockNode.addBreakPointIndexes(beginIndex+lastIndex+1);
//        }else{
//            blockNode.addBreakPointIndexes(-1);
//        }
//    }
}
