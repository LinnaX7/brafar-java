package sar.repair.utils;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.PrimitiveType;
import program.MethodBuilder;
import program.block.BlockNode;
import program.block.BlockType;
import sar.repair.Insert;
import variables.Variable;
import variables.VariableBuilder;
import variables.VariableMatch;

import java.util.*;

public class Utils {
    // class variable
    final static String lexicon = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    final static java.util.Random rand = new java.util.Random();

    // consider using a Map<String,Boolean> to say whether the identifier is being used or not
    final static Set<String> identifiers = new HashSet<String>();

    public static String randomIdentifier() {
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            for(int i = 0; i < 3; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }
    public static boolean isDef(Node node){
        if(node instanceof VariableDeclarator){
            return true;
        }else{
            for(Node child:node.getChildNodes()){
                if(isDef(child)){
                    return true;
                }
            }
        }
        return false;
    }
    public static List<VariableDeclarator> getVariableDecl(Node node){
        List<VariableDeclarator> decls=new ArrayList<>();
        if(node instanceof VariableDeclarator){
           decls.add((VariableDeclarator) node);
           return decls;
        }
        for(Node child:node.getChildNodes()){
            decls.addAll(getVariableDecl(child));
        }
        return decls;
    }
    public static int getDeclarationBlock(VariableBuilder variableBuilder, VariableDeclarator variableDeclarator){
        Variable v=variableBuilder.getVariable(variableDeclarator.getName().getIdentifier(),variableDeclarator);
        return v.getDefine();
    }
    //insert a statement into a blockStmt with an index
    public static void insertStmt(BlockStmt blockStmt, Statement statement, int index){
        if(index>blockStmt.getStatements().size()){
            index=blockStmt.getStatements().size();
        }
        if(index >= 0)
            blockStmt.getStatements().add(index,statement);
        NodeList<Statement> parentList=blockStmt.getStatements();
        for(int i=index+1;i >= 0 && i<parentList.size();i++){
            Statement stmt=parentList.get(i);
            //parentList.set(i,stmt);
            parentList.remove(i);
            parentList.add(i,stmt);
        }
    }
    public static void addMatch(String buggyName,Node buggyNode,String correctName,Node correctNode,Common common){
        common.variableMatch.addMatch(common.correctVariableBuilder.getVariable(correctName,correctNode),
                common.buggyVariableBuilder.getVariable(buggyName,buggyNode));

        VariableMatch.addPreMatch(buggyName,common.buggyVariableBuilder.getScopeIndex(Variable.getScope(buggyNode)),
                correctName,common.correctVariableBuilder.getScopeIndex(Variable.getScope(correctNode)));
    }
    public static Pair<Integer,Integer> getBlockRange(MethodBuilder methodBuilder,Integer metaBlockIndex) {
        if (metaBlockIndex < 0) {
            return new Pair<>(0, 0);
        }
        List<Integer>order=new ArrayList<>();
        BlockNode blockNode = methodBuilder.getMetaBlockNodes().get(metaBlockIndex);
        Node parent=blockNode.getParentNode();
        if(!(parent instanceof BlockStmt)){
            return new Pair<>(0,0);
        }
        for(Node child:blockNode.getTreeNodes()){
            assert child instanceof Statement;
            int index= -1;
           if(parent instanceof BlockStmt && child instanceof Statement)
                    getIndex(((BlockStmt) parent),(Statement) child);
            if(index!=-1)
                order.add(index);

        }
        if (blockNode.getJumpBlock() != null) {
            Node node = blockNode.getJumpBlock().getTreeNode();
            assert (node instanceof Statement);
            int index=getIndex(((BlockStmt) parent),(Statement) node);
            if(index!=-1)
                order.add(index);
        }
        order.sort(Comparator.naturalOrder());
        if(order.size()==0){
            return new Pair<>(blockNode.getTreeIndex(),blockNode.getTreeIndex()-1);
        }
        return new Pair<>(order.get(0),order.get(order.size()-1));

//        int first = -1, second = -1;
//        BlockNode blockNode = methodBuilder.getMetaBlockNodes().get(metaBlockIndex);
//        Node parent=blockNode.getParentNode();
//        if(!(parent instanceof BlockStmt)){
//            return new Pair<>(0,0);
//        }
//        if (blockNode.getTreeNodes().size() > 0) {
//            Node node = blockNode.getTreeNodes().get(0);
//            //first = node.getParentNode().get().getChildNodes().indexOf(node);
//            first=((BlockStmt)parent).getStatements().indexOf(node);
//            node = blockNode.getTreeNodes().get(blockNode.getTreeNodes().size() - 1);
//            //second = node.getParentNode().get().getChildNodes().indexOf(node);
//            second=((BlockStmt)parent).getStatements().indexOf(node);
//        }
//        if (blockNode.getJumpBlock() != null) {
//            Node node = blockNode.getJumpBlock().getTreeNode();
//            //int jump = node.getParentNode().get().getChildNodes().indexOf(node);
//            int jump=((BlockStmt)parent).getStatements().indexOf(node);
//            if(jump>second){
//                second=jump;
//            }
//            if (first == -1) {
//                first = second;
//            }
//        }
//        if(first==-1&&second==-1){
//            first=blockNode.getTreeIndex();
//            second=first-1;
//        }
//        return new Pair<>(first, second);
    }
    public static boolean isContain(Node def,Node use){
        if(def==use){
            return true;
        }
        boolean ret=false;
        for(Node child:def.getChildNodes()){
           ret =ret|isContain(child,use);
        }
        return ret;
    }
    public static boolean isDefBeforeUse(Variable b,int useIndex,Node useNode,Common common){
        if(b.getDefine()<common.blockIndex.peek()){
            Node def=b.getScope();
            BlockNode buggyBlock=common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek());
            if(buggyBlock.getTreeNodes().size()!=0){
                Node use=common.buggyMethodBuilder.getMetaBlockNodes().get(common.blockIndex.peek()).getTreeNodes().get(0);
                return isContain(def, use);
            }else{
                if(buggyBlock.getJumpBlock()!=null){
                    Node use=buggyBlock.getJumpBlock().getTreeNode();
                    return isContain(def, use);
                }
            }
        }else if(b.getDefine() == common.blockIndex.peek()){
            VariableDeclarator buggy=common.buggyVariableBuilder.getVariableDeclarator(b);
            assert buggy.getParentNode().isPresent();
            assert buggy.getParentNode().get() instanceof VariableDeclarationExpr;
            assert buggy.getParentNode().get().getParentNode().isPresent();
            if(buggy.getParentNode().isPresent() && buggy.getParentNode().get().getParentNode().isPresent() && buggy.getParentNode().get().getParentNode().get() instanceof  ExpressionStmt){
                //int index=getIndex(buggy.getParentNode().get().getParentNode().get());
                //if(index<useIndex){
                    Location key=null;
                    for(Map.Entry<Location,Node>entry:common.order.entrySet()){
                        if(entry.getValue()==useNode){
                           key=entry.getKey();
                           break;
                        }
                    }
                    if(key!=null){
                        common.order.remove(key);
                    }
                    return true;
                //}
            }
        }
        return false;
    }
    public static void checkDef(Variable b,Variable c,int useIndex,Node before,Common common,Node wholeNode){
        if(!isDefBeforeUse(b,useIndex,wholeNode,common)){
            //move def befor use
            VariableDeclarator buggy = common.buggyVariableBuilder.getVariableDeclarator(b);
            BlockNode buggyBlock=common.buggyMethodBuilder.getMetaBlockNodes().get(c.getDefine());
            Node parent=buggyBlock.getParentNode();
            if(parent instanceof BlockStmt){
                Node originalScope=b.getScope();
                int orignalDefBlock=b.getDefine();
                String orignalName=b.getName();
                VariableDeclarator newDef=buggy.clone();
                newDef.removeInitializer();
                ExpressionStmt newDefStmt=new ExpressionStmt(new VariableDeclarationExpr(newDef));
                Pair<Integer,Integer>range=getBlockRange(common.buggyMethodBuilder,c.getDefine());
                insertStmt((BlockStmt) parent,newDefStmt,range.first);
                buggyBlock.getTreeNodes().add(newDefStmt);
                common.order.put(new Location(c.getDefine(),-1),newDefStmt);
                common.buggyVariableBuilder.V2V.remove(b);
                common.buggyVariableBuilder.V2V.put(b,newDef);
                int newDefBlock=c.getDefine();
                b.setDefine(c.getDefine());
                Node newScope=Variable.getScope(newDefStmt);
                b.setScope(newScope);
                if((originalScope!=newScope||newDefBlock<orignalDefBlock)
                        &&!common.buggyVariableBuilder.isIdentical(b.getName())){
                    String randomName=orignalName+"_"+randomIdentifier();
                    changeNameInScope(before,newDef.getName().clone(),randomName,newDefBlock,common.buggyVariableBuilder);
                    changeNameInScope(originalScope,newDef.getName().clone(),randomName,newDefBlock,common.buggyVariableBuilder);
                    newDef.getName().setIdentifier(randomName);
                    b.setName(randomName);
                }
                VariableMatch.removePreMatch(orignalName,common.buggyVariableBuilder.getScopeIndex(originalScope));
                VariableMatch.addPreMatch(b.getName(),common.buggyVariableBuilder.getScopeIndex(b.getScope()),
                        c.getName(),common.correctVariableBuilder.getScopeIndex(c.getScope()));
            }else if(parent instanceof ForStmt){
               return;
            }
            //change original def to assign exp if necessary
            assert buggy.getParentNode().isPresent();
            assert buggy.getParentNode().get() instanceof VariableDeclarationExpr;
            assert buggy.getParentNode().get().getParentNode().isPresent();
            VariableDeclarationExpr buggyDecl=(VariableDeclarationExpr) buggy.getParentNode().get();
            assert buggyDecl.getParentNode().isPresent();
            if(buggyDecl.getParentNode().isPresent()){
                Node buggyStmt=buggyDecl.getParentNode().get();
            if(buggyDecl.getVariables().size()!=1){
                for(VariableDeclarator variableDeclarator:buggyDecl.getVariables()){
                    if(variableDeclarator.equals(buggy)){
                        buggyDecl.remove(buggy);
                        break;
                    }
                }
                if(buggy.getInitializer().isPresent()){
                    AssignExpr assignExpr=new AssignExpr(new NameExpr(buggy.getName()),buggy.getInitializer().get(), AssignExpr.Operator.ASSIGN);
                    if(buggyStmt instanceof ExpressionStmt){
                        assert buggyStmt.getParentNode().isPresent();
                        insertStmt((BlockStmt) (buggyStmt.getParentNode().get()),new ExpressionStmt(
                                assignExpr),getIndex(buggyStmt));
                    }else if(buggyStmt instanceof ForStmt){
                        if(buggyDecl.getVariables().size()==0){
                            ((ForStmt)buggyStmt).setInitialization(new NodeList<>(assignExpr));
                        }
                    }
                }
            }else{
                if(buggy.getInitializer().isPresent()) {
                    VariableDeclarationExpr assignExpr=new VariableDeclarationExpr(buggy);
                    buggyStmt.replace(buggyDecl,assignExpr);
                }
            }
            }
            
        }
    }
    public static void changeVar(Node buggy,Node correct,Common common){
        List<SimpleName>variableNames=getVariableNameFromNode(buggy);
        for(SimpleName simpleName:variableNames){
            String name = simpleName.getIdentifier();
            Variable c=common.correctVariableBuilder.getVariable(name,correct);
            if(c==null&&name.length()>4){
                c=common.correctVariableBuilder.getVariable(name.substring(0,name.length()-5), correct);
            }
            if(c!=null){
                if(common.variableMatch.getC2bMatch().containsKey(c)){
                    (simpleName).setIdentifier(common.variableMatch.getC2bMatch().get(c).getName());
                    boolean flag=true;
                    if(Utils.isDef(buggy)){
                        List<VariableDeclarator>decls=Utils.getVariableDecl(buggy);
                        for(VariableDeclarator var:decls){
                            if(var.getName().equals(simpleName)){
                                flag=false;
                            }
                        }
                    }
                    if(flag){
                        int index=-1;
                        for(Map.Entry<Location,Node>entry:common.order.entrySet()){
                            if(entry.getValue()==buggy){
                                Pair<Integer,Integer>range=getBlockRange(common.buggyMethodBuilder,entry.getKey().block);
                                index=range.first+entry.getKey().sequence;
                            }
                        }
                        if(index==-1){
                            assert (buggy.getParentNode().isPresent());
                            index=getIndex(buggy);
                            //index=buggy.getParentNode().get().getChildNodes().indexOf(buggy);
                        }
                        boolean f=true;
                        if(Utils.isDef(buggy)){
                            List<VariableDeclarator>decls=Utils.getVariableDecl(buggy);
                            for(VariableDeclarator var:decls){
                                if(var.getName().equals(simpleName)){
                                    f=false;
                                }
                            }
                        }
                        if(f)
                            Utils.checkDef(common.variableMatch.getC2bMatch().get(c),c,index,simpleName.getParentNode().get(),common,buggy);
                    }
                }else{
                    VariableDeclarator variableDeclarator=common.correctVariableBuilder.getVariableDeclarator(c);
                    int block= getDeclarationBlock(common.correctVariableBuilder,variableDeclarator);
                    Node parent=common.correctMethodBuilder.getMetaBlockNodes().get(block).getParentNode();
                    common.blockIndex.push(block);
                    Node correctDecl=variableDeclarator;
                    if(parent instanceof BlockStmt){
                        assert (variableDeclarator.getParentNode().isPresent());
                        assert (variableDeclarator.getParentNode().get().getParentNode().isPresent());
                        correctDecl= variableDeclarator.getParentNode().get().getParentNode().get();
                    }
                    ExpressionStmt expressionStmt=new ExpressionStmt(new VariableDeclarationExpr(variableDeclarator.clone()));
                    Insert insertStmt=new Insert(common);
                    insertStmt.insert(common.buggyMethodBuilder.getMetaBlockNodes().get(block).getParentNode(),expressionStmt,correctDecl);
                    //simpleName.setIdentifier(common.variableMatch.getC2bMatch().get(c).getName());
                    simpleName.setIdentifier(c.getName());
                    common.blockIndex.pop();
                }
            }
        }
    }
    public static void changeFloatToDouble(Common common){
        for(VariableDeclarator decl:common.buggyVariableBuilder.getVariableDeclarators()){
            if(decl.getType().isPrimitiveType()){
                if(decl.getType().asPrimitiveType().getType().name().equals("FLOAT")){
                    decl.setType(PrimitiveType.doubleType());
                }
            }
        }
//        if(buggyMethodBuilder.getMethodDeclaration().getType().isPrimitiveType()){
//            if(buggyMethodBuilder.getMethodDeclaration().getType().asPrimitiveType().getType().name().equals("FLOAT")){
//                buggyMethodBuilder.getMethodDeclaration().setType(PrimitiveType.doubleType());
//            }
//        }
    }
    public static void castReturnExp(Common common){
        //buggyVariableBuilder
        for(ReturnStmt returnStmt:common.buggyVariableBuilder.getReturnStmts()){
            assert (returnStmt.getExpression().isPresent());
            Expression exp=returnStmt.getExpression().get();
            if(exp instanceof CastExpr){
                if(((CastExpr)exp).getType().equals(common.buggyMethodBuilder.getMethodDeclaration().getType())){
                    continue;
                }
            }
            CastExpr castExpr=new CastExpr(common.buggyMethodBuilder.getMethodDeclaration().getType(), new EnclosedExpr(exp));
            returnStmt.setExpression(castExpr);
        }
    }
    public static void initDefinition(Common common){
        for(VariableDeclarator decl:common.buggyVariableBuilder.getVariableDeclarators()){
            if(decl.getInitializer().isEmpty()){
                if(decl.getType().isPrimitiveType()){
                    if(decl.getType().asPrimitiveType().getType().ordinal()==0){
                        decl.setInitializer("false");
                    }else{
                        decl.setInitializer("0");
                    }
                }else{
                    decl.setInitializer("null");
                }
            }
        }
    }
    public static void changeType(Node node,Node correct,Common common){
        if(!isDef(node)){
            return;
        }
        List<VariableDeclarator>decls=getVariableDecl(node);
        for(VariableDeclarator decl : decls){
            Variable c=common.correctVariableBuilder.getVariable(decl.getName().getIdentifier(),correct);
            if(common.variableMatch.getC2bMatch().containsKey(c)){
                Variable b=common.variableMatch.getC2bMatch().get(c);
                if(decl.getType().isPrimitiveType()&&b.getType().isPrimitiveType()){
                    if(decl.getType().asPrimitiveType().getType().ordinal()<
                            b.getType().asPrimitiveType().getType().ordinal()) {
                        decl.setType(b.getType());
                    }
                }
            }
        }
    }
    public static List<SimpleName> getVariableNameFromNode(Node node){
        List<SimpleName> ret=new ArrayList<>();
        if(node instanceof NameExpr){
            ret.add(((NameExpr)node).getName());
            return ret;
        }
        if(node instanceof SimpleName){
            //ret.add((SimpleName) node);
            assert node.getParentNode().isPresent();
            Node parent=node.getParentNode().get();
            if(parent instanceof VariableDeclarator){
                ret.add((SimpleName) node);
            }
        }
        for(Node child:node.getChildNodes()){
            ret.addAll(getVariableNameFromNode(child));
        }
        return ret;
    }
    public static int getIndex(BlockStmt parent,Statement node){
        int i=0;
        for(Statement child:parent.getStatements()){
            if(node==child){
                return i;
            }
            i++;
        }
        return -1;
    }
    public static int getIndex(List<Node>nodes,Node node){
        int i=0;
        for(Node child:nodes){
            if(child==node){
                return i;
            }
            i++;
        }
        return -1;
    }
    public static int getIndex(Node node){
        if(node.getParentNode().isPresent()){
            Node parent=node.getParentNode().get();
            if(parent instanceof BlockStmt && node instanceof Statement){
                return getIndex((BlockStmt) parent,(Statement) node);
            }
            return 0;
        }
        return -1;
    }
    public static void changeNameInScope(Node scope,SimpleName simpleName,String randomName,int newDef
            ,VariableBuilder variableBuilder){
        List<SimpleName>nameList=getVariableNameFromNode(scope);
        for(SimpleName name:nameList){
            if(name.equals(simpleName)){
                Variable var=variableBuilder.getVariable(name.getIdentifier(),name);
                if(var!=null&&var.getDefine()==newDef)
                    name.setIdentifier(randomName);
            }
        }
    }
}
