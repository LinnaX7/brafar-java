package sar;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import program.BlockBuilder;
import program.MethodBuilder;
import program.block.BlockNode;
import program.block.BlockType;
import program.block.JumpBlock;
import variables.*;

import java.io.*;
import java.util.*;



public class AFLW {
    private int rankUp=0;
    private final HashMap<BlockNode, BlockNode> blockMap;
    private final VariableBuilder buggyVariableBuilder;
    private BlockNode faultBlock;
    private boolean needClear;
    private final ArrayList<Integer> hasFixed;
    private boolean needStop;

    private final MethodBuilder buggyM;
    

    public AFLW(MethodBuilder buggyMethod, MethodBuilder correctMethod,VariableMatch variableMatch, ArrayList<Integer> hasFixed) throws IOException {
        buggyM = buggyMethod;
        blockMap = new HashMap<>();
        this.buggyVariableBuilder = buggyMethod.getVariableBuilder();
        this.hasFixed = hasFixed;
        faultBlock = null;
        needStop = false;
        blockAlign(buggyMethod.getBlockBuilder(), correctMethod.getBlockBuilder());
        compileErrorLocalization();
        if(faultBlock!=null){
            if(isBlockHasFixed(faultBlock))
                needStop = true;
        }else {
            faultLocalization(buggyMethod, correctMethod, variableMatch);
        }
        if(faultBlock == null)
            needStop = true;
        if(faultBlock!=null && isBlockHasFixed(faultBlock)){
            needStop = true;
        }
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
                        if(blockNode.getLineNumbers().contains(String.valueOf(lineNum))){
                            faultBlock = blockNode;
                            if(blockNode.getBlockType()==BlockType.WHILE_COND||blockNode.getBlockType()==BlockType.FOR_COMP){
                                Node cond = blockNode.getTreeNodes().get(0);
                                if(cond instanceof BooleanLiteralExpr)
                                    if(!((BooleanLiteralExpr) cond).getValue()){
                                        faultBlock = blockNode.getParentBlock();
                                        return;
                                    }
                            }
                            if(!hasFixed.contains(faultBlock.getMetaIndex()))
                                return;
                        }
                    }
                }
            }
            fStream.close();
            br.close();
        }
    }
    
    private AFL.Result isBlockHasFault(int i, VariableMatch variableMatch){
        AFL.Result result = new AFL.Result(false,false);
        for(Variable var: buggyVariableBuilder.getVariableList()) {
            Variable cVar = variableMatch.getB2cMatch().get(var);
            if (cVar != null) {
                Object varInV = var.getInValues().get(i);
                Object varOutV = var.getValues().get(i);
                Object cVarInV = cVar.getInValues().get(i);
                Object cVarOutV = cVar.getValues().get(i);
//                if(varInV.getClass() == ArrayList.class )
//                if(varInV !=null && cVarInV!=null && !varInV.equals(cVarInV))
//                    result.needRecall = true;
//                else  {
                if (varOutV != null) {
                    if (varOutV.equals(cVarOutV)) {
                        continue;
                    } else {
                        result.isFault = true;
                    }
                } else if (cVarOutV != null) {
                    result.isFault = true;
                }
//                }
            }
        }
        return result;
    }
    
    private void faultLocalization(MethodBuilder buggyMethod, MethodBuilder correctMethod,VariableMatch variableMatch){
        ArrayList<BlockNode> blockNodes = buggyMethod.getMetaBlockNodes();
        AFL.Result preResult = null;
        for (int i = 0; i < blockNodes.size();i++) {
            if(this.hasFixed.contains(i)) {
                continue;
            }
            BlockNode currentBlock = blockNodes.get(i);
            AFL.Result result = null;
            switch (currentBlock.getBlockType()){
                case BASIC_BLOCK:
                case EMPTY_BLOCK:
                case FOR_INIT:
                case FOR_UPDATE:
                    result= isBlockHasFault(i, variableMatch);
                    if(currentBlock.getBlockType()==BlockType.EMPTY_BLOCK && blockMap.get(currentBlock).getBlockType()==BlockType.EMPTY_BLOCK
                            && currentBlock.getJumpBlock() == null && blockMap.get(currentBlock).getJumpBlock()==null)
                        result = new AFL.Result(false,false);
                    break;
                case IF_COND:
                case WHILE_COND:
                case FOR_COMP:
                case FOREACH_ITER:
                    if(!currentBlock.getCondValues().equals(blockMap.get(currentBlock).getCondValues())){
                        result = new AFL.Result(true, false);
                        if(hasFixed.contains(i))
                            result.setNeedRecall(true);
                    }
                    else
                        result = new AFL.Result(false,false);
                    break;
            }
            preResult = result;
            assert result != null;
            if(result.needRecall){
                int temp = i;
                while(temp> 0) {
                    if (!hasFixed.contains(temp - 1)){
                        faultBlock = blockNodes.get(temp-1);
                        break;
                    }
                    temp --;
                }
                if(faultBlock == null){
                    temp = i+1;
                    while(temp<blockNodes.size()) {
                        if (!hasFixed.contains(temp)){
                            faultBlock = blockNodes.get(temp);
                            break;
                        }
                        temp ++;
                    }
                }
                if(faultBlock!=null)
                    break;
            }
            if(result.isFault){
                faultBlock = currentBlock;
                if(currentBlock.getBlockType().equals(BlockType.WHILE_COND)){
                    if(blockMap.get(currentBlock).getParentBlock().getChildBlocks().get(1).getChildBlocks().get(0).getEmpty())
                        if(!hasFixed.contains(i+1))
                            faultBlock = currentBlock.getParentBlock().getChildBlocks().get(1).getChildBlocks().get(0);
                }

                if(currentBlock.getBlockType().equals(BlockType.IF_COND)){
                    if(blockMap.get(currentBlock).getParentBlock().getChildBlocks().get(1).getChildBlocks().get(0).getEmpty())
                        if(!hasFixed.contains(i+1))
                            faultBlock = currentBlock.getParentBlock().getChildBlocks().get(1).getChildBlocks().get(0);
                }
                break;
            }
        }
        if(faultBlock == null){
            for (int i = 0; i < blockNodes.size();i++) {
                if(!hasFixed.contains(i))
                    faultBlock = blockNodes.get(i);
            }
        }
    }

    public BlockNode getFaultBlock() {
        return faultBlock;
    }

    private void blockAlign(BlockBuilder buggyBB, BlockBuilder correctBB){
        for (int i = 0; i < buggyBB.getBlockNodes().size(); i++) {
            blockMap.put(buggyBB.getBlockNodes().get(i), correctBB.getBlockNodes().get(i));
            blockAlignChildren(buggyBB.getBlockNodes().get(i), correctBB.getBlockNodes().get(i));
        }
    }

    private void blockAlignChildren(BlockNode buggyBlock, BlockNode correctBlock){
        for (int i = 0; i < buggyBlock.getChildBlocks().size(); i++) {
            blockMap.put(buggyBlock.getChildBlocks().get(i), correctBlock.getChildBlocks().get(i));
            blockAlignChildren(buggyBlock.getChildBlocks().get(i), correctBlock.getChildBlocks().get(i));
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


}


