package sar;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import program.BlockBuilder;
import program.MethodBuilder;
import program.block.BlockNode;
import program.block.BlockType;
import program.StackBuffer;
import variables.*;

import java.io.*;
import java.util.*;

import static program.BlockBuilder.*;


public class AFL {
    private final HashMap<Integer, ArrayList<Variable>> rankedVariable;
    private int rankUp=0;
    private final HashMap<BlockNode, BlockNode> blockMap;
    private final Set<Variable> suspiciousVariables;

    private final Set<Variable> suspiciousCorrectVariables;
    private final VariableBuilder buggyVariableBuilder;
    private BlockNode faultBlock;
    private boolean needClear;
    private final ArrayList<Integer> hasFixed;
    private boolean needStop;

    private final MethodBuilder buggyM;



    public static class VariableComparator implements Comparator<Variable> {
        public int compare(Variable var1, Variable var2) {
            if(var1== var2)
                return 0;
            return -1;
        }
    }

    public void updateVariableRank(Variable variable, VariableMatch variableMatch){
        if(variableMatch.getB2cMatch().containsKey(variable))
            variable.setRank(Math.min(variable.getRank(), variableMatch.getB2cMatch().get(variable).getRank()));
    }

    public boolean isNeedClear() {
        return needClear;
    }

    public AFL(MethodBuilder buggyMethod, MethodBuilder correctMethod,VariableMatch variableMatch, ArrayList<Integer> hasFixed) throws IOException {
        buggyM = buggyMethod;
        rankedVariable = new HashMap<>();
        blockMap = new HashMap<>();
        suspiciousVariables = new TreeSet<>(new VariableComparator());
        suspiciousCorrectVariables = new LinkedHashSet<>();
        this.buggyVariableBuilder = buggyMethod.getVariableBuilder();
        this.hasFixed = hasFixed;
        faultBlock = null;
        needClear = false;
        needStop = false;
        blockAlign(buggyMethod.getBlockBuilder(), correctMethod.getBlockBuilder());
        compileErrorLocalization();
        if(faultBlock!=null){
            if(isBlockHasFixed(faultBlock))
                needStop = true;
        }else {
            initRankedVariable(buggyMethod.getVariableBuilder().getVariableList(), variableMatch);
            faultLocalization(buggyMethod, correctMethod, variableMatch);
        }
        if(faultBlock == null)
            needStop = true;
    }

    private void compileErrorLocalization() throws IOException {
        if(new File(Fixer.COMPILE_LOG_PATH).exists()) {
            FileInputStream fStream = new FileInputStream(Fixer.COMPILE_LOG_PATH);
            BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
            String line = br.readLine();
            if (line != null) {
                if(line.startsWith(buggyM.getProgramBuilder().getFilePath())){
                    int index = buggyM.getProgramBuilder().getFilePath().length()+1;
                    int lineNum = 0;
                    for (int i = index; i < line.length(); i++) {
                        char ch = line.charAt(i);
                        if(ch<'0' || ch>'9') break;
                        lineNum = lineNum*10+ch-'0';
                    }
                    for(BlockNode blockNode:buggyM.getMetaBlockNodes()){
                        if(blockNode.getLineNumbers().contains(String.valueOf(lineNum))) {
                            faultBlock = blockNode;
                            if (!hasFixed.contains(faultBlock.getMetaIndex())) {
                                if (blockNode.getBlockType() == BlockType.WHILE_COND || blockNode.getBlockType() == BlockType.FOR_COMP) {
                                    Node cond = blockNode.getTreeNodes().get(0);
                                    if (cond instanceof BooleanLiteralExpr)
                                        if (!((BooleanLiteralExpr) cond).getValue()) {
                                            faultBlock = blockNode.getParentBlock();
                                            return;
                                        }
                                }
                                return;
                            }
                        }
                    }
                }
            }
            fStream.close();
            br.close();
        }
    }
    private void setFaultBlock(BlockNode blockNode){
        faultBlock=blockNode;
    }

    public boolean isNeedStop() {
        return needStop;
    }

    private boolean isBlockHasFixed(BlockNode blockNode){
        if(blockNode.getMetaIndex() !=-1) {
            return hasFixed.contains(blockNode.getMetaIndex());
        }else{
            for(BlockNode child:blockNode.getChildBlocks()){
                if(!isBlockHasFixed(child))
                    return false;
            }
        }
        return true;
    }

    private Result faultLocalizationBody(ArrayList<BlockNode> buggyBlockChildren, StackBuffer buggyStackBuffer,
                                       ArrayList<BlockNode> correctBlockChildren, StackBuffer correctStackBuffer,
                                       VariableMatch variableMatch){
        ArrayList<ValueIndex> buggyBlockNodesValueIndexes = getChildrenIndexes(buggyBlockChildren, buggyStackBuffer);
        ArrayList<ValueIndex> correctBlockNodesValueIndexes = getChildrenIndexes(correctBlockChildren, correctStackBuffer);
        Result preResult = new Result(false, false);
        for (int i = 0; i < buggyBlockChildren.size();) {
            BlockNode currentBlock = buggyBlockChildren.get(i);
            Result result = isBlockHasFault( currentBlock, buggyBlockNodesValueIndexes.get(i),
                    correctBlockNodesValueIndexes.get(i), variableMatch);
            if(result.needRecall) {
                if(i ==0)
                    return result;
                preResult = result;
                i -= 1;
                continue;
            }
            if(result.isFault){
                int endIndex;
                if(buggyBlockNodesValueIndexes.get(i).outValueIndex != -1)
                    endIndex = buggyBlockNodesValueIndexes.get(i).outValueIndex;
                else endIndex = buggyStackBuffer.endIndex;
                StackBuffer buggyBlockStackBuffer = new StackBuffer(buggyBlockNodesValueIndexes.get(i).inValueIndex,endIndex, buggyStackBuffer.breakPointLineIndexes);
                buggyBlockStackBuffer.setOutIndex(buggyBlockNodesValueIndexes.get(i).outValueIndex);
                if(correctBlockNodesValueIndexes.get(i).outValueIndex!=-1)
                    endIndex = correctBlockNodesValueIndexes.get(i).outValueIndex;
                else endIndex = correctStackBuffer.endIndex;
                StackBuffer correctBlockStackBuffer = new StackBuffer(correctBlockNodesValueIndexes.get(i).inValueIndex, endIndex, correctStackBuffer.breakPointLineIndexes);
                correctBlockStackBuffer.setOutIndex(correctBlockNodesValueIndexes.get(i).outValueIndex);
                switch (currentBlock.getBlockType()){
                    case BASIC_BLOCK:
                    case EMPTY_BLOCK:
                        setFaultBlock(currentBlock);
                        break;
                    case IF_BLOCK:
                        faultLocalizationIF(currentBlock, buggyBlockStackBuffer, blockMap.get(currentBlock), correctBlockStackBuffer, variableMatch);
                        break;
                    case FOR_BLOCK:
                        faultLocalizationFor(currentBlock,buggyBlockStackBuffer, blockMap.get(currentBlock), correctBlockStackBuffer, variableMatch);
                        break;
                    case FOREACH_BLOCK:
                        faultLocalizationForEach(currentBlock,buggyBlockStackBuffer, blockMap.get(currentBlock), correctBlockStackBuffer, variableMatch);
                        break;
                    case WHILE_BLOCK:
                        faultLocalizationWhile(currentBlock,buggyBlockStackBuffer, blockMap.get(currentBlock), correctBlockStackBuffer, variableMatch);
                        break;
                }
            }
            if(faultBlock!=null){
                if(isBlockHasFixed(faultBlock))
                    needStop = true;
                return result;
            }
            else if(preResult.needRecall) {
                faultBlock = currentBlock;
                if(isBlockHasFixed(faultBlock))
                    needStop = true;
                return result;
            }else {
                preResult = result;
                i++;
            }
        }
        return preResult;
    }


    private void faultLocalization(MethodBuilder buggyMethod, MethodBuilder correctMethod, VariableMatch variableMatch) {
        faultLocalizationBody(buggyMethod.getBlockBuilder().getBlockNodes(), new StackBuffer(0, buggyMethod.getBreakPointLines().size(), buggyMethod.getBreakPointLines()),
                correctMethod.getBlockBuilder().getBlockNodes(), new StackBuffer(0, correctMethod.getBreakPointLines().size(), correctMethod.getBreakPointLines()),
                variableMatch);
    }

    private boolean needRepairAll(BlockNode buggyBlock, StackBuffer buggyStackBuffer, BlockNode correctBlock,
                                  StackBuffer correctStackBuffer){
        boolean buggyBlockEmptyFlag = getBodyFlag(buggyBlock, buggyStackBuffer);
        boolean correctBlockEmptyFlag = getBodyFlag(correctBlock, correctStackBuffer);

        if(!buggyBlockEmptyFlag){
            //buggyForBlock is Empty
            //this.faultBlock = buggyBlock;
            setFaultBlock(buggyBlock);
            return true;
        }
        if(!correctBlockEmptyFlag){
            //correctForBlock is Empty
            //this.faultBlock = buggyBlock;
            setFaultBlock(buggyBlock);
            this.needClear = true;
            return true;
        }
        return false;
    }



    private void isForInitUpdateFault(BlockNode forInitBlock, BlockNode buggyForBody, StackBuffer buggyStackBuffer, BlockNode correctForBody,
                                StackBuffer correctStackBuffer,VariableMatch variableMatch){
        int index = getBlockValueBeginIndex(buggyForBody, buggyStackBuffer);
        int index2 = getBlockValueBeginIndex(correctForBody, correctStackBuffer);
        for(Variable var:forInitBlock.getRelatedVars()){
            if(variableMatch.getB2cMatch().get(var)==null) {
                //faultBlock = forInitBlock;
                setFaultBlock(forInitBlock);
                return;
            }
            Variable cVar = variableMatch.getB2cMatch().get(var);
            if(getVariableValue(var, index)==null) {
                //faultBlock = forInitBlock;
                setFaultBlock(forInitBlock);
                return;
            }
            Object val1 = getVariableValue(var, index);
            Object val2 = getVariableValue(cVar,index2);
            if(val1==null && val2!=null){
                //faultBlock = forInitBlock;
                setFaultBlock(forInitBlock);
                return;
            }
            if(val1!=null && !isEqual(val1,val2)){
                setFaultBlock(forInitBlock);
                return;
            }
            if(val2!=null && !isEqual(val2,val1)){
                setFaultBlock(forInitBlock);
                return;
            }
        }
    }

    private void normalLoopBodyFaultLocalization(BlockNode buggyLoopBlock, StackBuffer buggyStackBuffer, BlockNode correctLoopBlock,
                                                 StackBuffer correctStackBuffer,VariableMatch variableMatch){
        BlockNode buggyLoopBody = buggyLoopBlock.getChildBlocks().get(1);
        BlockNode correctLoopBody = correctLoopBlock.getChildBlocks().get(1);
        ArrayList<Integer> forEachBlockIndexes = getLoopIndexes(buggyLoopBlock, buggyStackBuffer);
        ArrayList<Integer> correctForEachBlockIndexes = getLoopIndexes(correctLoopBlock, correctStackBuffer);

        for (int i = 0; i < Math.min(forEachBlockIndexes.size(), correctForEachBlockIndexes.size()); i++) {
            int buggyBegin = forEachBlockIndexes.get(i)+1;
            int buggyEnd = buggyStackBuffer.endIndex;
            StackBuffer buggyBodyStackBuffer = new StackBuffer(buggyBegin, buggyEnd, buggyStackBuffer.breakPointLineIndexes);
            if(i < forEachBlockIndexes.size()-1) {
                buggyBodyStackBuffer.setEndIndex(forEachBlockIndexes.get(i + 1));
                buggyBodyStackBuffer.setOutIndex(forEachBlockIndexes.get(i + 1));
            }else{
                buggyBodyStackBuffer.setOutIndex(buggyStackBuffer.outIndex);
            }
            int correctBegin = correctForEachBlockIndexes.get(i);
            int correctEnd = correctStackBuffer.endIndex;
            StackBuffer correctBodyStackBuffer = new StackBuffer(correctBegin, correctEnd, correctStackBuffer.breakPointLineIndexes);
            if(i<correctForEachBlockIndexes.size()-1) {
                correctBodyStackBuffer.setOutIndex(correctForEachBlockIndexes.get(i + 1));
                correctBodyStackBuffer.setEndIndex(correctForEachBlockIndexes.get(i + 1));
            }else{
                correctBodyStackBuffer.setOutIndex(correctStackBuffer.outIndex);
            }
            faultLocalizationBody(buggyLoopBody.getChildBlocks(), buggyBodyStackBuffer, correctLoopBody.getChildBlocks(), correctBodyStackBuffer, variableMatch);
            if(faultBlock!=null)
                return;
        }
    }

    private void forBodyFaultLocalization(BlockNode buggyLoopBlock, StackBuffer buggyStackBuffer, BlockNode correctLoopBlock,
                                                 StackBuffer correctStackBuffer,VariableMatch variableMatch){
        BlockNode buggyForInit = buggyLoopBlock.getChildBlocks().get(0);
        BlockNode correctForInit = correctLoopBlock.getChildBlocks().get(0);
        BlockNode buggyUpdateBlock = buggyLoopBlock.getChildBlocks().get(2);
        BlockNode buggyLoopBody = buggyLoopBlock.getChildBlocks().get(3);
        BlockNode correctUpdateBlock = correctLoopBlock.getChildBlocks().get(2);
        BlockNode correctLoopBody = correctLoopBlock.getChildBlocks().get(3);
        ArrayList<Integer> loopBlockIndexes = getLoopIndexes(buggyLoopBlock, buggyStackBuffer);
        ArrayList<Integer> correctLoopBlockIndexes = getLoopIndexes(correctLoopBlock, correctStackBuffer);

        for (int i = 0; i < Math.min(loopBlockIndexes.size(), correctLoopBlockIndexes.size()); i++) {
            int buggyBegin = loopBlockIndexes.get(i)+1;
            int buggyEnd = buggyStackBuffer.endIndex;
            StackBuffer buggyBodyStackBuffer = new StackBuffer(buggyBegin, buggyEnd, buggyStackBuffer.breakPointLineIndexes);
            if(i < loopBlockIndexes.size()-1) {
                buggyBodyStackBuffer.setEndIndex(loopBlockIndexes.get(i + 1));
                buggyBodyStackBuffer.setOutIndex(loopBlockIndexes.get(i + 1));
            }else{
                buggyBodyStackBuffer.setOutIndex(buggyStackBuffer.outIndex);
            }
            int correctBegin = correctLoopBlockIndexes.get(i)+1;
            int correctEnd = correctStackBuffer.endIndex;
            StackBuffer correctBodyStackBuffer = new StackBuffer(correctBegin, correctEnd, correctStackBuffer.breakPointLineIndexes);
            if(i<correctLoopBlockIndexes.size()-1) {
                correctBodyStackBuffer.setOutIndex(correctLoopBlockIndexes.get(i + 1));
                correctBodyStackBuffer.setEndIndex(correctLoopBlockIndexes.get(i + 1));
            }else{
                correctBodyStackBuffer.setOutIndex(correctStackBuffer.endIndex);
            }
            if(i==0){
                isForInitUpdateFault(buggyForInit, buggyLoopBody, buggyBodyStackBuffer, correctLoopBody, correctBodyStackBuffer, variableMatch);
            }
            else{
                isForInitUpdateFault(buggyUpdateBlock,buggyLoopBody, buggyBodyStackBuffer, correctLoopBody, correctBodyStackBuffer, variableMatch);
            }
            if(faultBlock!=null)
                return;
            Result result = faultLocalizationBody(buggyLoopBody.getChildBlocks(), buggyBodyStackBuffer, correctLoopBody.getChildBlocks(), correctBodyStackBuffer, variableMatch);

            if(faultBlock!=null)
                return;
            if(i!=0 && result.needRecall)
                i -= 2;
        }
    }



    private void faultLocalizationFor(BlockNode buggyForBlock, StackBuffer buggyStackBuffer, BlockNode correctForBlock,
                                      StackBuffer correctStackBuffer,VariableMatch variableMatch){
        if (needRepairAll(buggyForBlock, buggyStackBuffer, correctForBlock, correctStackBuffer))
            return;
        //buggyFor is not empty and correctFor is not empty
        if(buggyStackBuffer.getOriginalIndex(buggyForBlock.getChildBlocks().get(0).getLastLineNumber())!=-1&&
        correctStackBuffer.getOriginalIndex(correctForBlock.getChildBlocks().get(0).getLastLineNumber())!=-1){
            if(getBodyFlag(correctForBlock.getChildBlocks().get(3), correctStackBuffer)) {
                if(getBodyFlag(buggyForBlock.getChildBlocks().get(3), buggyStackBuffer)) {
                    forBodyFaultLocalization(buggyForBlock, buggyStackBuffer, correctForBlock, correctStackBuffer, variableMatch);
                    if (faultBlock != null)
                        return;
                }
                //buggyForCondition has fault
                BlockNode forCond = buggyForBlock.getChildBlocks().get(1);
//            if(!hasFixed.contains(forCond.getMetaIndex()))
//                faultBlock = forCond;
                if(!isBlockHasFixed(forCond)) {
                    setFaultBlock(forCond);
                    return;
                }else{
                    //buggyForBody is Empty
                    setFaultBlock(buggyForBlock.getChildBlocks().get(3));
                    return;
                }
            }
            else{
                BlockNode forCond = buggyForBlock.getChildBlocks().get(1);
//            if(!hasFixed.contains(forCond.getMetaIndex()))
//                faultBlock = forCond;
                setFaultBlock(forCond);
                return;
            }
        }
        //unNormal loop
        //faultBlock = buggyForBlock;
        setFaultBlock(buggyForBlock);
        BlockNode forInitBlock= buggyForBlock.getChildBlocks().get(0);
        BlockNode forCompBlock = buggyForBlock.getChildBlocks().get(1);
        BlockNode forBody = buggyForBlock.getChildBlocks().get(3);
    }

    private void faultLocalizationForEach(BlockNode buggyForEachBlock, StackBuffer buggyStackBuffer, BlockNode correctForEachBlock,
                                          StackBuffer correctStackBuffer,VariableMatch variableMatch) {
        if (needRepairAll(buggyForEachBlock, buggyStackBuffer, correctForEachBlock, correctStackBuffer))
            return;
        BlockNode forIterBlock = buggyForEachBlock.getChildBlocks().get(0);
        BlockNode forBody = buggyForEachBlock.getChildBlocks().get(1);
        BlockNode correctForBody = correctForEachBlock.getChildBlocks().get(1);
        isForInitUpdateFault(forIterBlock, forBody, buggyStackBuffer, correctForBody, correctStackBuffer, variableMatch);
        if(faultBlock!=null)
            return;
        normalLoopBodyFaultLocalization(buggyForEachBlock, buggyStackBuffer, correctForEachBlock, correctStackBuffer, variableMatch);
    }

    private void faultLocalizationWhile(BlockNode buggyWhileBlock, StackBuffer buggyStackBuffer, BlockNode correctWhileBlock,
                                        StackBuffer correctStackBuffer,VariableMatch variableMatch) {
        if (needRepairAll(buggyWhileBlock, buggyStackBuffer, correctWhileBlock, correctStackBuffer))
            return;
        if(buggyStackBuffer.getOriginalIndex(buggyWhileBlock.getChildBlocks().get(0).getLastLineNumber())!=-1&&
                correctStackBuffer.getOriginalIndex(correctWhileBlock.getChildBlocks().get(0).getLastLineNumber())!=-1){
            if(getBodyFlag(correctWhileBlock.getChildBlocks().get(1), correctStackBuffer)) {
                //while cond is not empty/true or false and body is not empty
                if(getBodyFlag(buggyWhileBlock.getChildBlocks().get(1), buggyStackBuffer)) {
                    normalLoopBodyFaultLocalization(buggyWhileBlock, buggyStackBuffer, correctWhileBlock, correctStackBuffer, variableMatch);
                }
                if (faultBlock == null) {
                    //body doesn't have fault, cond fault
                    BlockNode whileCond = buggyWhileBlock.getChildBlocks().get(0);
                    if(!hasFixed.contains(whileCond.getMetaIndex())) {
                        setFaultBlock(whileCond);
                        return;
                    }else{
                        //while block is empty
                        setFaultBlock(buggyWhileBlock.getChildBlocks().get(1));
                        return;
                    }
                }
                return;

            }else{
                BlockNode whileCond = buggyWhileBlock.getChildBlocks().get(0);
//                if(!hasFixed.contains(whileCond.getMetaIndex()))
//                    faultBlock = whileCond;
                setFaultBlock(whileCond);
                return;
            }
        }
        //unNormal while
        //faultBlock = buggyWhileBlock;
        setFaultBlock(buggyWhileBlock);
    }


    private void blockAlignChildren(BlockNode buggyBlock, BlockNode correctBlock){
        for (int i = 0; i < buggyBlock.getChildBlocks().size(); i++) {
            blockMap.put(buggyBlock.getChildBlocks().get(i), correctBlock.getChildBlocks().get(i));
            blockAlignChildren(buggyBlock.getChildBlocks().get(i), correctBlock.getChildBlocks().get(i));
        }
    }

    private void blockAlign(BlockBuilder buggyBB, BlockBuilder correctBB){
        for (int i = 0; i < buggyBB.getBlockNodes().size(); i++) {
            blockMap.put(buggyBB.getBlockNodes().get(i), correctBB.getBlockNodes().get(i));
            blockAlignChildren(buggyBB.getBlockNodes().get(i), correctBB.getBlockNodes().get(i));
        }
    }

    private void initRankedVariable(List<Variable> variables, VariableMatch variableMatch){
        for(Variable var:variables){
            updateVariableRank(var, variableMatch);
            int rank = var.getRank();
            if(rankedVariable.containsKey(rank)){
                rankedVariable.get(rank).add(var);
            }else{
                if(rank>this.rankUp)
                    this.rankUp = rank;
                ArrayList<Variable> newVars = new ArrayList<>();
                newVars.add(var);
                rankedVariable.put(rank, newVars);
            }
        }

        if(rankedVariable.size()==0)
            return;
        if(rankedVariable.containsKey(0))
            this.suspiciousVariables.addAll(rankedVariable.get(0));
        if(rankedVariable.size()>1)
            if(rankedVariable.containsKey(1))
                suspiciousVariables.addAll(rankedVariable.get(1));
    }


    public BlockNode getFaultBlock() {
        return faultBlock;
    }

    public HashMap<BlockNode, BlockNode> getBlockMap() {
        return blockMap;
    }

    static class Result{
        boolean needRecall;
        boolean isFault;

        public Result(boolean isFault, boolean needRecall){
            this.needRecall = needRecall;
            this.isFault = isFault;
        }

        public void setFault(boolean fault) {
            isFault = fault;
        }

        public void setNeedRecall(boolean needRecall) {
            this.needRecall = needRecall;
        }
    }





    private Object getVariableValue(Variable var, int index) {
        if (index != -1) {
            return var.getLineValue(index);
        } else {
            return null;
        }
    }

    private ArrayList<Variable> getVariableDependenciesFromMatrix(GraphAdjacencyMatrix matrix, Variable var, VariableBuilder variableBuilder){
        List<Integer> relyV = matrix.getRely(variableBuilder.getIndex(var));
        ArrayList<Variable> relyVars = new ArrayList<>();
        for (int i: relyV) {
            relyVars.add(variableBuilder.getVariable(i));
        }
        return relyVars;
    }

    public Set<Variable> getBlockRelyVars(BlockNode buggyBlock, GraphAdjacencyMatrix matrix, GraphAdjacencyMatrix matrix2,VariableMatch variableMatch, Variable errVar){
        Set<Variable> relyV = new LinkedHashSet<>(getVariableDependenciesFromMatrix(matrix, errVar, buggyVariableBuilder));
        if(variableMatch.getB2cMatch().containsKey(errVar)){
            Set<Variable> correctVs = new LinkedHashSet<>(getVariableDependenciesFromMatrix(matrix2, variableMatch.getB2cMatch().get(errVar), variableMatch.getCorrectVariableBuilder()));
            Set<Variable> visitedVars = new LinkedHashSet<>();
            while (correctVs.size()!=0) {
                visitedVars.addAll(correctVs);
                Set<Variable> susVars = new LinkedHashSet<>();
                for (Variable cv : correctVs) {
                    if(variableMatch.getC2bMatch().containsKey(cv))
                        relyV.add(variableMatch.getC2bMatch().get(cv));
                    else {
                        if(buggyBlock.getMetaIndex()!=-1) {
                            this.suspiciousCorrectVariables.add(cv);
                        }
                        ArrayList<Variable> correctVs2 = getVariableDependenciesFromMatrix(matrix2, cv, variableMatch.getCorrectVariableBuilder());
                        susVars.addAll(correctVs2);
                    }
                }
                susVars.removeAll(visitedVars);
                correctVs = susVars;
            }
        }
        return relyV;
    }

    public Set<Variable> getBlockRelyVars(BlockNode buggyBlock, GraphAdjacencyMatrix matrix, GraphAdjacencyMatrix matrix2,VariableMatch variableMatch, Set<Variable> errorVars){

        Set<Variable> relyV = new LinkedHashSet<>();
        for (Variable errVar : errorVars) {
            relyV.addAll(getBlockRelyVars(buggyBlock, matrix, matrix2, variableMatch, errVar));
        }
        return relyV;
    }

    public Set<Variable> getBlockRealRelyVars(GraphAdjacencyMatrix matrix, VariableMatch variableMatch, Set<Variable> errorVars){

        Set<Variable> relyV = new LinkedHashSet<>();
        for (Variable errVar : errorVars) {
            if(variableMatch.getB2cMatch().containsKey(errVar)) {
                relyV.addAll(getVariableDependenciesFromMatrix(matrix, errVar, buggyVariableBuilder));
            }
        }
        return relyV;
    }

    private boolean isCharEqualOther(Object var1,Object var2){
        char v1 = ((String) var1).charAt(0);
        if(var2 instanceof Integer){
            return (v1-(Integer)var2)==0;
        }
        if(var2 instanceof Float){
            return (v1-(Float)var2)==0;
        }
        if(var2 instanceof Double){
            return (v1-(Double)var2)==0;
        }
        return false;
    }

    private boolean isEqual(Object var1, Object var2){
        if(var1.getClass().equals(java.lang.Integer.class) && (var2!=null && var2.getClass().equals(java.lang.Double.class))){
            return Double.valueOf((Integer) var1).equals(var2);
        }else if(var1.getClass().equals(java.lang.Double.class) && (var2!=null && var2.getClass().equals(java.lang.Integer.class)))
            return Double.valueOf((Integer) var2).equals(var1);
        else if(var1.getClass().equals(java.lang.String.class) && (var2!=null && !var2.getClass().equals(java.lang.String.class))){
            return isCharEqualOther(var1,var2);
        }else if(!var1.getClass().equals(java.lang.String.class) && (var2!=null && var2.getClass().equals(java.lang.String.class))){
            return isCharEqualOther(var2, var1);
        }
        else
            return var1.equals(var2);

    }

    private Result isBlockHasFault(BlockNode buggyBlockNode, ValueIndex buggyBlockValueIndexes, ValueIndex correctValueIndexes, VariableMatch variableMatch){
        Result result = new Result(false, false);
        Set<Variable> errorVars = new LinkedHashSet<>();
        Set<Variable> visitedV = new LinkedHashSet<>();
        boolean needGoBackToDef = false;
        Set<Variable> defErrorVars = new LinkedHashSet<>();
        if(buggyBlockValueIndexes.getInValueIndex()==-1){
            result.setFault(true);
            return result;
        }
        if(buggyBlockValueIndexes.getInValueIndex()==buggyBlockValueIndexes.getOutValueIndex()){
            if(buggyBlockNode.getBlockType()==BlockType.EMPTY_BLOCK){
                if(buggyBlockNode.getJumpBlock()==null && blockMap.get(buggyBlockNode).getJumpBlock()!=null){
                    result.setFault(true);
                    return result;
                }
                if(buggyBlockNode.getJumpBlock()!=null && blockMap.get(buggyBlockNode).getJumpBlock()==null){
                    result.setFault(true);
                    return result;
                }
            }
            else{
                if(correctValueIndexes.getInValueIndex()!=correctValueIndexes.getOutValueIndex()){
                    if(isBlockHasFixed(buggyBlockNode)){
                        //previous block occurs exception
                        result.setNeedRecall(true);
                    }
                    result.setFault(true);
                    return result;
                }
            }
        }
        if(buggyBlockValueIndexes.getOutValueIndex()==-1||correctValueIndexes.getOutValueIndex()==-1) {
            errorVars.addAll(this.buggyVariableBuilder.getVariableList());
        }else if(buggyBlockNode.getJumpBlock()!=null){
            if(buggyBlockNode.getJumpBlock().getType()==BlockType.RETURN||blockMap.get(buggyBlockNode).getJumpBlock()==null){
                errorVars.addAll(this.suspiciousVariables);
            } else if(buggyBlockNode.getJumpBlock().getType()!=blockMap.get(buggyBlockNode).getJumpBlock().getType()){
                errorVars.addAll(this.suspiciousVariables);
            }
        }else if(blockMap.get(buggyBlockNode).getJumpBlock()!=null){
            errorVars.addAll(this.suspiciousVariables);
        }else {
            if(correctValueIndexes.getInValueIndex()==-1) {
                GraphAdjacencyMatrix matrix = VariableDependency.getGraphAdjacencyMatrixFromBlock( buggyBlockNode, buggyVariableBuilder);
                GraphAdjacencyMatrix matrix2 = VariableDependency.getGraphAdjacencyMatrixFromBlock(blockMap.get(buggyBlockNode), variableMatch.getCorrectVariableBuilder());
                Set<Variable> relyV = getBlockRelyVars(buggyBlockNode,matrix,matrix2, variableMatch, this.suspiciousVariables);
                this.suspiciousVariables.addAll(relyV);
                result.setNeedRecall(true);
                result.setFault(true);
                return result;
            }
            for (Variable var : this.suspiciousVariables) {
                Object buggyInValue = getVariableValue(var, buggyBlockValueIndexes.getInValueIndex());
                Object buggyValue = getVariableValue(var, buggyBlockValueIndexes.getOutValueIndex());
                if (variableMatch.getB2cMatch().get(var) == null) {
                    continue;
                }
                Variable correctVar = variableMatch.getB2cMatch().get(var);
                Object correctInValue = getVariableValue(correctVar, correctValueIndexes.getInValueIndex());
                Object correctValue = getVariableValue(correctVar, correctValueIndexes.getOutValueIndex());

                if (correctInValue == null && correctValue == null)
                    continue;
                if (buggyInValue == null && buggyValue == null) {
                    if(var.getDefine()==buggyBlockNode.getMetaIndex())
                        errorVars.add(var);
//                    if(correctVar.getDefine()!=blockMap.get(buggyBlockNode).getMetaIndex() && correctInValue != correctVar){
//                        errorVars.add(var);
//                    }
                    continue;
                }
                if(buggyInValue != null && correctInValue!=null){
                    if(!isEqual(buggyInValue, correctInValue)){
                        result.setNeedRecall(true);
                        result.setFault(true);
                        return result;
                    }
                }
                if (buggyValue != null && !isEqual(buggyValue, correctValue)) {
                    if(buggyInValue == null&&correctInValue!=null){
                        //In this case, this is the definition of buggyVar, need to fix the defBlock of buggyVar.
//                        faultBlock = buggyM.getMetaBlockNodes().get(correctVar.getDefine());
//                        result.setNeedRecall(true);
//                        return result;
                        defErrorVars.add(var);
                        needGoBackToDef = true;
                    }
                    errorVars.add(var);
                }
            }
            visitedV.addAll(this.suspiciousVariables);
        }
        if(errorVars.size()==0){
            boolean flag = false;
            for(Variable cv:this.suspiciousCorrectVariables){
                Object correctInValue = getVariableValue(cv, correctValueIndexes.getInValueIndex());
                if(correctInValue!=null)
                    flag = true;
            }
            result.setFault(false);
            result.setNeedRecall(flag);
            if(!flag) {
                for (Variable cv : this.suspiciousCorrectVariables) {
                    Object correctValue = getVariableValue(cv, correctValueIndexes.getOutValueIndex());
                    if (correctValue != null) {
                        result.setNeedRecall(false);
                        result.setFault(true);
                        return result;
                    }
                }
            }
        }else {
            GraphAdjacencyMatrix matrix = VariableDependency.getGraphAdjacencyMatrixFromBlock( buggyBlockNode, buggyVariableBuilder);
            GraphAdjacencyMatrix matrix2 = VariableDependency.getGraphAdjacencyMatrixFromBlock(blockMap.get(buggyBlockNode), variableMatch.getCorrectVariableBuilder());
            Set<Variable> relyV = getBlockRelyVars(buggyBlockNode,matrix, matrix2, variableMatch, errorVars);
            Set<Variable> realRelyV = getBlockRealRelyVars(matrix, variableMatch, errorVars);
            relyV.addAll(errorVars);
            relyV.removeAll(visitedV);
            if (relyV.size() == 0&&this.suspiciousCorrectVariables.size()==0) {
                result.setFault(true);
                if(needGoBackToDef){
                    faultBlock = buggyM.getMetaBlockNodes().get(variableMatch.getB2cMatch().get(defErrorVars.iterator().next()).getDefine());
                    result.setFault(false);
                    return result;
                }
//                result.setNeedRecall(false);
            } else {
                boolean flag = result.needRecall;
                while(relyV.size()!=0) {
                    visitedV.addAll(relyV);
                    Set<Variable> suspiciousNewVars = new LinkedHashSet<>();
                    for (Variable rv : relyV) {
                        if (variableMatch.getB2cMatch().get(rv) == null)
                            continue;
                        Variable crv = variableMatch.getB2cMatch().get(rv);
                        Object buggyInValue = getVariableValue(rv, buggyBlockValueIndexes.getInValueIndex());
                        Object correctInValue = getVariableValue(crv, correctValueIndexes.getInValueIndex());
                        Object buggyValue = getVariableValue(rv, buggyBlockValueIndexes.getOutValueIndex());
                        Object correctValue = getVariableValue(crv, correctValueIndexes.getOutValueIndex());
                        if(correctInValue == null && correctValue == null)
                            continue;
                        if (buggyInValue != null && correctInValue!=null && !isEqual(buggyInValue, correctInValue)) {
                            suspiciousVariables.add(rv);
                            flag = true;
                            continue;
                        }
                        if (buggyValue != null && !isEqual(buggyValue, correctValue)) {
                            suspiciousNewVars.addAll(getBlockRelyVars(buggyBlockNode, matrix,matrix2,variableMatch, rv));
                            if(buggyInValue == null){
                                needGoBackToDef = true;
                                defErrorVars.add(rv);
                                continue;
                            }
                        }
                        if(!realRelyV.contains(rv)&&buggyInValue==null&&buggyValue!=null){
                            faultBlock = buggyM.getMetaBlockNodes().get(crv.getDefine());
                            return result;
                        }
                    }
                    suspiciousNewVars.removeAll(visitedV);
                    relyV = suspiciousNewVars;
                }
                result.setNeedRecall(flag);
                result.setFault(true);
                for(Variable cv:suspiciousCorrectVariables){
                    Object correctInValue = getVariableValue(cv, correctValueIndexes.getInValueIndex());
                    if(isBlockHasFixed(buggyBlockNode)&&correctInValue!=null)
                        flag = true;
                }
                result.setNeedRecall(flag);
                result.setFault(true);
                int min = 10000;
                if(needGoBackToDef&&!flag){
                    for (Variable variable:defErrorVars) {
                        int defIn = variableMatch.getB2cMatch().get(variable).getDefine();
                        if (defIn < min)
                            min = defIn;
                    }
                    faultBlock = buggyM.getMetaBlockNodes().get(min);
                    result.setFault(false);
                    return result;
                }
            }
        }
        return result;
    }

    private void faultLocalizationEmptyBody(ArrayList<BlockNode> bodyChildren){
        for (BlockNode child:bodyChildren) {
            switch (child.getBlockType()){
                case BASIC_BLOCK:
                case EMPTY_BLOCK:
//                    if(!hasFixed.contains(child.getMetaIndex()))
//                        faultBlock = child;
                    setFaultBlock(child);
                    break;
                case IF_BLOCK:
                    faultLocalizationEmptyIf(child);
                    break;
                case FOR_BLOCK:
                case WHILE_BLOCK:
                case FOREACH_BLOCK:
                    faultLocalizationEmptyLoop(child);
            }
            if(faultBlock!=null)
                return;
        }
    }

    private void faultLocalizationEmptyIf(BlockNode ifBlock) {
        BlockNode ifCond = ifBlock.getChildBlocks().get(0);
        if (!hasFixed.contains(ifCond.getMetaIndex()))
            faultBlock = ifCond;
        else
            faultLocalizationEmptyBody(ifBlock.getChildBlocks().get(1).getChildBlocks());
        if (faultBlock == null && ifBlock.getChildBlocks().size() == 3)
            faultLocalizationEmptyBody(ifBlock.getChildBlocks().get(2).getChildBlocks());
    }

    private void faultLocalizationEmptyLoop(BlockNode loopBlock) {
        //faultBlock = loopBlock;
        setFaultBlock(loopBlock);
    }


    private void faultLocalizationIF(BlockNode buggyIfBlock, StackBuffer buggyStackBuffer, BlockNode correctIfBlock,
                                     StackBuffer correctStackBuffer, VariableMatch variableMatch) {
        BlockNode ifCond = buggyIfBlock.getChildBlocks().get(0);
        BlockNode ifBody = buggyIfBlock.getChildBlocks().get(1);
        BlockNode correctIfCond = correctIfBlock.getChildBlocks().get(0);
        BlockNode correctIfBody = correctIfBlock.getChildBlocks().get(1);

        boolean buggyCondFlag = getCondFlag(ifCond, ifBody, buggyStackBuffer);
        boolean buggyIfBodyFlag = getBodyFlag(ifBody, buggyStackBuffer);
        boolean correctCondFlag = getCondFlag(correctIfCond, correctIfBody, correctStackBuffer);
        boolean correctIfBodyFlag = getBodyFlag(correctIfBody, correctStackBuffer);

        if(buggyStackBuffer.outIndex==-1&& !buggyIfBodyFlag) {
            if(buggyIfBlock.getChildBlocks().size()==3){
                boolean buggyElseBodyFlag = getBodyFlag(buggyIfBlock.getChildBlocks().get(2), buggyStackBuffer);
                if(!buggyElseBodyFlag) {
                    setFaultBlock(ifCond);
                    return;
                }
            }else {
                setFaultBlock(ifCond);
                return;
            }
        }
        if (buggyCondFlag != correctCondFlag) {
            if (!hasFixed.contains(ifCond.getMetaIndex())) {
                faultBlock = ifCond;
            }else{//condition has fixed, so this happens when buggyIfBody is empty or correctIfBody is empty
                if(correctCondFlag) {
                    //buggyIfBody is empty
                    faultLocalizationEmptyBody(ifBody.getChildBlocks());
                }else{
                    //correctIfBody is empty
//                    faultBlock = ifBody;
                    setFaultBlock(ifBody);
                    needClear = true;
                }
            }
        }
        else{
            if(correctCondFlag){
                if(!correctIfBodyFlag){
                    //correctIfBody is empty
                    if(buggyIfBodyFlag) {
                        //buggyIfBody is not Empty
                        //faultBlock = ifBody;
                        setFaultBlock(ifBody);
                        needClear = true;
                    }
                }
                else{
                    if(!buggyIfBodyFlag){
                        //buggyIfBody is Empty
                        if(hasFixed.contains(ifBody.getParentBlock().getChildBlocks().get(0).getMetaIndex())){
                            setFaultBlock(ifBody.getChildBlocks().get(0));
                        }else
                            setFaultBlock(ifBody.getParentBlock());
                    }
                    else {
                        faultLocalizationBody(ifBody.getChildBlocks(), buggyStackBuffer, correctIfBody.getChildBlocks(), correctStackBuffer, variableMatch);
                    }
                }
            }else{
                if(buggyIfBlock.getChildBlocks().size()==3) {
                    //BuggyIfBlock and correctIfBlock has no elseBranch, error will not happen, so they both have else branch.
                    BlockNode buggyElseBody = buggyIfBlock.getChildBlocks().get(2);
                    BlockNode correctElseBody = correctIfBlock.getChildBlocks().get(2);
                    if (!getBodyFlag(correctElseBody, correctStackBuffer)) {
                        //correctIfBlock's elseBody is empty
                        //faultBlock = buggyElseBody;
                        setFaultBlock(buggyElseBody);
                        needClear = true;
                    } else {
                        if (!getBodyFlag(buggyElseBody, buggyStackBuffer)) {
                            //buggyIfBlock's elseBody is empty
                            //faultBlock = buggyElseBody;
                            setFaultBlock(buggyElseBody);
                            needClear = true;
                        } else {
                            faultLocalizationBody(buggyElseBody.getChildBlocks(), buggyStackBuffer, correctElseBody.getChildBlocks(), correctStackBuffer, variableMatch);
                        }
                    }
                }
            }
        }
    }
}


