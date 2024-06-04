package program;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import program.block.BlockNode;
import program.block.BlockType;
import program.block.JumpBlock;
import variables.Variable;
import variables.VariableBuilder;

import java.io.*;
import java.util.*;

public class MethodBuilder {
    private final String methodName;
    private final BlockBuilder blockBuilder;
    //private final VariableBuilder variables;
    private final VariableBuilder variableBuilder;
    private int blockIndex;
    private final ArrayList<BlockNode> metaBlockNodes;//
    private ArrayList<String> breakPointLines;
    private final MethodDeclaration methodDeclaration;

    private ProgramBuilder programBuilder;

    private final ArrayList<JumpBlock> returnBlocks;

    public int getMethodLOC(){
        assert methodDeclaration.getEnd().isPresent();
        assert methodDeclaration.getBegin().isPresent();
        return methodDeclaration.getEnd().get().line-methodDeclaration.getBegin().get().line+1;
    }

    public void setProgramBuilder(ProgramBuilder programBuilder) {
        this.programBuilder = programBuilder;
    }

    public ProgramBuilder getProgramBuilder() {
        return programBuilder;
    }

    public MethodBuilder(MethodDeclaration methodDeclaration) {
        this.methodDeclaration=methodDeclaration;
        this.methodName = methodDeclaration.getName().toString();
        blockBuilder = new BlockBuilder(methodDeclaration);
        variableBuilder =new VariableBuilder(methodDeclaration);
        blockIndex = 0;
        metaBlockNodes = new ArrayList<>();
        returnBlocks = new ArrayList<>();
        //variables = new VariableBuilder(methodDeclaration);
        SetVariablesDU();
//        System.out.println(blockIndex);
    }
    public MethodDeclaration getMethodDeclaration() {
        return methodDeclaration;
    }
    public String getMethodName() {
        return methodName;
    }

    public ArrayList<String> getBreakPointLines() {
        return breakPointLines;
    }

//    public VariableBuilder getVariables() {
//        return variables;
//    }
    public VariableBuilder getVariableBuilder(){
        return variableBuilder;
    }

    public void clearBlocksBLP(){
        blockBuilder.clearBlockBPL();
    }

    public void setVariableValues(String executeLog, String packageName) throws IOException {
        for(Variable var: variableBuilder.getVariableList()) {
           var.getLineValues().clear();
        }
        this.breakPointLines = new ArrayList<>();
        FileInputStream fStream = new FileInputStream(executeLog);
        BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
        String line = br.readLine();
        int index = 0;
        while (line!=null){
            if(line.startsWith(String.format("Variables at %s:", packageName))){
                String lineNumber = line.split(":")[1];
                br.readLine();
                line = br.readLine();
                while (!line.equals("]")){
                    line.replaceAll("[\b\r\n\t\u0000\u0001]*", "");
                    JSONObject var;
                    try {
                        var = JSONObject.fromObject(line);
                    }catch (JSONException e){
                        int i = line.indexOf("\"value");
                        line = line.substring(0,63)+"\"value\":\"\"}";
//                        System.out.println(line);
                        var = JSONObject.fromObject(line);
                    }
                    String name = var.getString("name");
                    if(variableBuilder.isIdentical(name)){
                        variableBuilder.getVariable(name).putLineValues(index,var.get("value"));
                    }else{
                        variableBuilder.getVariable(name, Integer.parseInt(lineNumber)).putLineValues(index,var.get("value"));
                    }

                    line = br.readLine();
                }
                breakPointLines.add(lineNumber);
                index+=1;
            }
            line = br.readLine();
        }
        blockBuilder.setBodyChildrenBPL(null,0, breakPointLines.size(), breakPointLines);
        setVariableBlockValue();
    }


    public void setVariableBlockValue(){
        for(Variable var: variableBuilder.getVariableList()) {
            var.getInValues().clear();
            var.getValues().clear();
            for (int i = 0; i < blockIndex; i++) {
                ArrayList<Integer> indexes = metaBlockNodes.get(i).getBreakPointIndexes();
                ArrayList<Integer> inIndexes = metaBlockNodes.get(i).getBreakPointInIndexes();
                var.addValuesFromIndexes(indexes, inIndexes);
            }
//            System.out.println(var.getName());
//            System.out.println(var.getInValues());
//            System.out.println(var.getValues());
        }
    }

    public void analyseExp(int blk,Expression expression) {
        if (expression instanceof VariableDeclarationExpr) {
            ((VariableDeclarationExpr) expression).getVariables().forEach(v -> {
                Variable variable = variableBuilder.getVariable(v.getName().getIdentifier(),expression);
                if(variable != null) {
                    variable.setDefine(blk);
                    variable.addDefUseMark(metaBlockNodes.get(blk).getBlockMark());
                    metaBlockNodes.get(blk).addRelatedVar(variable);
                }
//                System.out.print("Define:");
//                System.out.print(v.getName().getIdentifier());
//                System.out.print(" Block:[");
//                System.out.print(variable.getDefine());
//                System.out.println("]");
            });
        }else if(expression instanceof NameExpr) {
            Variable variable = variableBuilder.getVariable(((NameExpr) expression).getName().getIdentifier(),expression);
            if (variable != null) {
                variable.setUses(blk);
                variable.addDefUseMark(metaBlockNodes.get(blk).getBlockMark());
                metaBlockNodes.get(blk).addRelatedVar(variable);
            }
//            System.out.print("Uses:");
//            System.out.print(((NameExpr) expression).getName().getIdentifier());
//            System.out.print(" Block:");
//            System.out.println(variable.getUses());
        }
    }

    public void visitNode(int blk,Node n){
        if( n instanceof IfStmt){
            visitNode(blk,((IfStmt)n).getCondition());
            return;
        } else if(n instanceof WhileStmt){
            visitNode(blk,((WhileStmt)n).getCondition());
            return;
        } else if(n instanceof SwitchStmt){
            visitNode(blk,((SwitchStmt)n).getSelector());
            return;
        }else if(n instanceof  Expression){
            analyseExp(blk,(Expression) n);
        }else if(n instanceof ReturnStmt){
            ReturnStmt returnStmt=(ReturnStmt) n;
            if(returnStmt.getExpression().isPresent()) {
                Expression exp=returnStmt.getExpression().get();
                Variable variable= variableBuilder.getReturnVariable(exp);
                variable.setDefine(blk);
                variable.setReturnUses(blk);
                variable.addDefUseMark(metaBlockNodes.get(blk).getBlockMark());
                variable.addDefUseMark(metaBlockNodes.get(blk).getJumpBlock().getBlockMark());
                //variable.addDefUseMark(metaBlockNodes.get(blk).getBlockMark());
                metaBlockNodes.get(blk).addRelatedVar(variable);
                //variable.addValues(blk);
                for(NameExpr nameExpr:returnStmt.getExpression().get().findAll(NameExpr.class)) {
                    Variable var = variableBuilder.getVariable(nameExpr.getName().getIdentifier(), returnStmt.getExpression().get());
                    if (var != null) {
                        var.setReturnUses(blk);
                        var.addDefUseMark(metaBlockNodes.get(blk).getJumpBlock().getBlockMark());
                        metaBlockNodes.get(blk).addRelatedVar(var);
                        metaBlockNodes.get(blk).getJumpBlock().addRelatedVar(var);
                    }

                }
            }
        }
        for(Node node:n.getChildNodes()){
            visitNode(blk,node);
        }
    }

    public void setVariableUsage(BlockNode blockNode){

        if(blockNode.getChildBlocks().size()==0){
            metaBlockNodes.add(blockNode);
            blockNode.setMetaIndex(blockIndex);
            if(blockNode.getJumpBlock()!=null && blockNode.getJumpBlock().getType()== BlockType.RETURN){
                returnBlocks.add(blockNode.getJumpBlock());
                blockNode.getJumpBlock().setMetaIndex(blockIndex);
            }
            if(blockNode.getEmpty() && (blockNode.getJumpBlock()!=null && blockNode.getJumpBlock().getType()== BlockType.RETURN)){
                visitNode(blockIndex, blockNode.getJumpBlock().getTreeNode());

            }else if(!blockNode.getEmpty()){
                blockNode.getTreeNodes().forEach(node -> visitNode(blockIndex, node));
                if(blockNode.getJumpBlock()!=null && blockNode.getJumpBlock().getType()==BlockType.RETURN)
                    visitNode(blockIndex, blockNode.getJumpBlock().getTreeNode());
            }
            blockIndex += 1;
        }else{
            for(BlockNode child:blockNode.getChildBlocks()){
                setVariableUsage(child);
            }
        }
    }

    public ArrayList<JumpBlock> getReturnBlocks() {
        return returnBlocks;
    }

    public void SetVariablesDU() {
        //System.out.println("DU");
        for(BlockNode blockNode: blockBuilder.getBlockNodes()){
            setVariableUsage(blockNode);
        }
    }

    public BlockBuilder getBlockBuilder() {
        return blockBuilder;
    }

    public ArrayList<BlockNode> getMetaBlockNodes() {
        return metaBlockNodes;
    }

    /*
    public void setIfBlockBPL(ArrayList<String> breakPointLine){
        for (BasicBlock bln: basicBlockBuilder.getBlockNodes()) {
            if (bln.getEmpty())
                continue;
            String lastLine = bln.getLineNumbers().get(bln.getLineNumbers().size() - 1);
            if (!breakPointLine.contains(lastLine)) {
                return;
            }
            int index = breakPointLine.indexOf(lastLine);
            if (bln.getBlockType() == BlockType.IF_COND) {
                int nextIndex;
                int count = 0;
                do {
                    bln.addBreakPointInIndexes(index);
                    if(index+1<breakPointLine.size()){
                        bln.addBreakPointIndexes(index+1);
                    }else{
                        bln.addBreakPointIndexes(-1);
                    }
                    if(bln.getElseBlockBegin()!=-1){
                        String lineNumber = "";
                        int i;
                        for (i = bln.getElseBlockBegin(); i < bln.getElseBlockEnd()+1; i++) {
                            if(basicBlockBuilder.getBlockNodes().get(i).getLineNumbers().size()>0)
                                break;
                        }
                        if(i!=bln.getElseBlockEnd()+1){
                            lineNumber = basicBlockBuilder.getBlockNodes().get(i).getLineNumbers().get(0);
                        }
                        if(Integer.parseInt(breakPointLine.get(index+1)) < Integer.parseInt(lineNumber)){
                            for (i = bln.getElseBlockBegin(); i < bln.getElseBlockEnd()+1; i++) {
                                BasicBlock n = basicBlockBuilder.getBlockNodes().get(i);
                                n.getBreakPointInIndexes().add(count, -1);
                                n.getBreakPointIndexes().add(count, -1);
                            }
                        }else{
                            for (int j = basicBlockBuilder.getBlockNodes().indexOf(bln)+1; j < bln.getElseBlockBegin(); j++) {
                                BasicBlock n = basicBlockBuilder.getBlockNodes().get(j);
                                n.getBreakPointInIndexes().add(count, -1);
                                n.getBreakPointIndexes().add(count, -1);
                            }
                        }
                    }
                    nextIndex = breakPointLine.subList(index+1, breakPointLine.size()).indexOf(lastLine);
                    index = index + nextIndex +1 ;
                    count += 1;
                } while(nextIndex != -1);
            }
        }
    }

    public void setEmptyBlockBPL(ArrayList<String> breakPointLine){
        for (int i = 0; i < basicBlockBuilder.getBlockNodes().size(); i++) {
            BasicBlock bln = basicBlockBuilder.getBlockNodes().get(i);
            if (bln.getEmpty()) {
                if (bln.getJumpBlock() != null && bln.getJumpBlock().getType() != BlockType.EMPTY_STMT) {
                    int index = breakPointLine.indexOf(bln.getJumpBlock().getLineNumber());
                    bln.addBreakPointInIndexes(index);
                    if(bln.getJumpBlock().getType() == BlockType.EMPTY_STMT){
                        bln.addBreakPointIndexes(index);
                    }else {
                        bln.addBreakPointIndexes(-1);
                    }
                }else{
                    if(i+1<basicBlockBuilder.getBlockNodes().size() && basicBlockBuilder.getBlockNodes().get(i + 1).getParentBlock() == bln.getParentBlock()) {
                        bln.getBreakPointIndexes().addAll(basicBlockBuilder.getBlockNodes().get(i + 1).getBreakPointInIndexes());
                        bln.getBreakPointInIndexes().addAll(basicBlockBuilder.getBlockNodes().get(i + 1).getBreakPointInIndexes());
                    }else if(bln.getInBlocks().size()!=0){
                        BasicBlock blk1 = bln.getInBlocks().get(0);
                        for (int j = 0; j < blk1.getBreakPointIndexes().size(); j++) {
                            int nn;
                            for(BasicBlock bb:bln.getInBlocks()){
                                if(bb.getBreakPointIndexes().get(j)!=-1){
                                    nn = bb.getBreakPointIndexes().get(j);
                                    bln.addBreakPointInIndexes(nn);
                                    bln.addBreakPointIndexes(nn);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public String getCompositeBlockBeginLine(int i){
        BasicBlock nextBlock = basicBlockBuilder.BlockNodes.get(i +1);
        if(nextBlock.getEmpty() && nextBlock.getJumpBlock()==null){
            nextBlock = basicBlockBuilder.BlockNodes.get(i +2);
        }
        String line2;
        if(nextBlock.getEmpty()){
            line2 = nextBlock.getJumpBlock().getLineNumber();
        }else{
            line2 = nextBlock.getLineNumbers().get(0);
        }
        return line2;
    }

    public void setBlockBPL(ArrayList<String> breakPointLine){
        for (int i = 0; i < basicBlockBuilder.getBlockNodes().size(); i++) {
            BasicBlock bln = basicBlockBuilder.getBlockNodes().get(i);
            if(bln.getEmpty() || bln.getBlockType()==BlockType.IF_COND) {
                continue;
            }
            String lastLine = bln.getLineNumbers().get(bln.getLineNumbers().size()-1);
            if(!breakPointLine.contains(lastLine)){
                continue;
            }
            int index = breakPointLine.indexOf(lastLine);
            switch (bln.getBlockType()){
                case BASIC_BLOCK:
                    int nextIndex;
                    do {
                        bln.addBreakPointInIndexes(index);
                        if(index+1<breakPointLine.size()){
                            bln.addBreakPointIndexes(index+1);
                        }else{
                            bln.addBreakPointIndexes(-1);
                        }
                        nextIndex = breakPointLine.subList(index+1, breakPointLine.size()).indexOf(lastLine);
                        index = index + nextIndex +1 ;
                    } while(nextIndex != -1);
                    break;
                case FOREACH_ITER:
                case WHILE_COND:
                    String line2 = getCompositeBlockBeginLine(i);
                    do {
                        bln.addBreakPointInIndexes(index);
                        int index2 = breakPointLine.subList(index+1,breakPointLine.size()).indexOf(line2);
                        if(index2 !=-1){
                            bln.addBreakPointIndexes(index+index2+1);
                        }else{
                            bln.addBreakPointIndexes(-1);
                        }
                        nextIndex = breakPointLine.subList(index+1, breakPointLine.size()).indexOf(lastLine);
                        index = index + nextIndex +1 ;
                    } while(nextIndex != -1);
                    break;
                case FOR_INIT:
                    line2 = getCompositeBlockBeginLine(i+2);
                    bln.addBreakPointInIndexes(index);
                    int index2 = breakPointLine.subList(index+1,breakPointLine.size()).indexOf(line2);
                    if(index2 !=-1){
                        bln.addBreakPointIndexes(index+index2+1);
                    }else{
                        bln.addBreakPointIndexes(-1);
                    }
                    break;
                case FOR_COMP:
                    line2 = getCompositeBlockBeginLine(i+1);
                    nextIndex = breakPointLine.subList(index+1,breakPointLine.size()).indexOf(line2);
                    if(nextIndex!=-1) {
                        do{
                            index = index+nextIndex +1;
                            bln.addBreakPointInIndexes(index);
                            bln.addBreakPointIndexes(index);
                            nextIndex = breakPointLine.subList(index+1,breakPointLine.size()).indexOf(line2);
                        }while (nextIndex!=-1);
                    }else{
                        bln.addBreakPointInIndexes(-1);
                        bln.addBreakPointIndexes(-1);
                    }
                    break;
                case FOR_UPDATE:
                    line2 = getCompositeBlockBeginLine(i);
                    index2 = breakPointLine.subList(index+1,breakPointLine.size()).indexOf(line2);
                    if(index2!=-1) {
                        index = index+index2+1;
                        nextIndex = breakPointLine.subList(index+1,breakPointLine.size()).indexOf(line2);
                        do {
                            index = index + nextIndex + 1;
                            bln.addBreakPointInIndexes(index - 1);
                            bln.addBreakPointIndexes(index);
                            nextIndex = breakPointLine.subList(index+1,breakPointLine.size()).indexOf(line2);
                        }while (nextIndex!=-1);

                    }else{
                        bln.addBreakPointIndexes(-1);
                        bln.addBreakPointIndexes(-1);
                    }
                    break;
            }
        }
        setIfBlockBPL(breakPointLine);
        setEmptyBlockBPL(breakPointLine);
    }
    */

}
