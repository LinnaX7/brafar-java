package variables;

import com.github.gumtreediff.utils.Pair;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import program.MethodBuilder;
import program.block.BlockNode;
import program.block.BlockType;
import program.block.JumpBlock;
import com.github.javaparser.ast.Node;
import sar.repair.utils.Utils;
import java.util.*;

public class VariableMatch {
    public static double MINIMUM_DEF_USE_MATCH_RATE = 0.7;

    HashMap<Variable,Variable>c2bMatch;
    HashMap<Variable,Variable>b2cMatch;
    VariableBuilder correctVariableBuilder;
    VariableBuilder buggyVariableBuilder;

    boolean[][]map;
    int N,M;
    boolean[] vis;
    int[] p;

    List<Variable>UnMatchC;
    List<Variable>UnMatchB;
    Map<Integer,List<Integer>> CUnMatchRely;
    Map<Integer,List<Integer>> CUnMatchRelied;
    Map<Integer,List<Integer>> BUnMatchRely;
    Map<Integer,List<Integer>> BUnMatchRelied;

    MethodBuilder buggyM;
    MethodBuilder correctM;
    static Map<Pair<String,Integer>,Pair<String,Integer>> preMatch=new LinkedHashMap<>();
    public VariableMatch(MethodBuilder buggyM, MethodBuilder correctM){
        this.buggyM=buggyM;
        this.correctM=correctM;

        CUnMatchRely=new HashMap<>();
        CUnMatchRelied=new HashMap<>();
        BUnMatchRely=new HashMap<>();
        BUnMatchRelied=new HashMap<>();

        c2bMatch=new HashMap<>();
        b2cMatch=new HashMap<>();

        UnMatchC=new ArrayList<>();
        UnMatchB=new ArrayList<>();

        buggyVariableBuilder=buggyM.getVariableBuilder();
        correctVariableBuilder=correctM.getVariableBuilder();
        UnMatchC.addAll(correctVariableBuilder.getVariableList());
        UnMatchB.addAll(buggyVariableBuilder.getVariableList());
        //pre match
        for(Map.Entry<Pair<String,Integer>,Pair<String,Integer>>entry:preMatch.entrySet()){
            Variable b=buggyVariableBuilder.getVariableByIndex(entry.getKey().first,entry.getKey().second);
            Variable c=correctVariableBuilder.getVariableByIndex(entry.getValue().first,entry.getValue().second);
            if(b!=null && c!=null) {
                c2bMatch.put(c, b);
                b2cMatch.put(b, c);
                UnMatchB.remove(b);
                UnMatchC.remove(c);
            }
        }
        //param match
        for(int i=0;i<buggyVariableBuilder.getParamSize();i++){
            Variable c=correctVariableBuilder.getParamList().get(i);
            Variable b=buggyVariableBuilder.getParamList().get(i);
            c2bMatch.put(c,b);
            b2cMatch.put(b,c);
            UnMatchC.remove(c);
            UnMatchB.remove(b);
        }
        //Return Var match
        for (int i = 0; i < buggyM.getReturnBlocks().size(); i++) {
            if (buggyM.getReturnBlocks().get(i).getRelatedVars().size() == 1) {
                Variable var = buggyM.getReturnBlocks().get(i).getRelatedVars().iterator().next();
                if (UnMatchB.contains(var)) {
                    int metaIndex = buggyM.getReturnBlocks().get(i).getMetaIndex();
                    if(metaIndex >= correctM.getMetaBlockNodes().size()) continue;
                    BlockNode correctBlock = correctM.getMetaBlockNodes().get(metaIndex);
                    if (correctBlock.getJumpBlock() != null && correctBlock.getJumpBlock().getType() == BlockType.RETURN) {
                        JumpBlock retBlock = correctBlock.getJumpBlock();
                        if (retBlock.getRelatedVars().size() == 1) {
                            Variable var2 = retBlock.getRelatedVars().iterator().next();
                            if (UnMatchC.contains(var2)) {
                                if(canVariableMatch(var, var2)) {
                                    c2bMatch.put(var2, var);
                                    b2cMatch.put(var, var2);
                                    UnMatchC.remove(var2);
                                    UnMatchB.remove(var);
                                }
                            }
                        }
                    }
                }
            }
            ReturnStmt returnStmt = (ReturnStmt) buggyM.getReturnBlocks().get(i).getTreeNode();
            if (returnStmt.getExpression().isPresent()) {
                Variable r1 = buggyVariableBuilder.getReturnVariable(returnStmt.getExpression().get());
                if(UnMatchB.contains(r1)) {
                    int metaIndex = buggyM.getReturnBlocks().get(i).getMetaIndex();
                    BlockNode correctBlock = correctM.getMetaBlockNodes().get(metaIndex);
                    if (correctBlock.getJumpBlock() != null && correctBlock.getJumpBlock().getType() == BlockType.RETURN) {
                        ReturnStmt returnStmt1 = (ReturnStmt) correctBlock.getJumpBlock().getTreeNode();
                        if (returnStmt1.getExpression().isPresent()) {
                            Variable r2 = correctVariableBuilder.getReturnVariable(returnStmt1.getExpression().get());
                            if (UnMatchC.contains(r2)) {
                                c2bMatch.put(r2, r1);
                                b2cMatch.put(r1, r2);
                                UnMatchC.remove(r2);
                                UnMatchB.remove(r1);
                            }
                        }
                    }
                }
            }
        }

        //DEA & DUA match
//        matchVariablesByDEA();
        matchVariablesByDUA();
        matchVariablesByDUA(buggyM, 0.9);
        matchVariablesByDUA(buggyM, 0.8);
        matchVariablesByDUA(buggyM, 0.7);
        matchVariablesByDUA(buggyM, 0.6);
        //rely on match
        matchVariablesByRely();

        assert b2cMatch.size()==c2bMatch.size();
        System.out.println("--------------------------------------Variable match--------------------------------------");
        for(Map.Entry<Variable,Variable>entry:b2cMatch.entrySet()){
            //Change buggy code type to match correct code
           if(!(entry.getKey().specialType.equals(Variable.SpecialType.RETURN)
                   &&entry.getValue().specialType.equals(Variable.SpecialType.RETURN))){
               if(entry.getKey().getType().isPrimitiveType()&&entry.getValue().getType().isPrimitiveType()){
                   //if(entry.getKey().getType().asPrimitiveType().getType().ordinal()<entry.getValue().getType().asPrimitiveType().getType().ordinal()) {

                       VariableDeclarator b=buggyVariableBuilder.getVariableDeclarator(entry.getKey());
                       VariableDeclarator c=correctVariableBuilder.getVariableDeclarator(entry.getValue());
                       if(b!=null&&c!=null&&b.getParentNode().isPresent()){
                           Node parent=b.getParentNode().get();
                           if(parent instanceof VariableDeclarationExpr) {
                               if (((VariableDeclarationExpr) parent).getVariables().size() == 1) {
                                   b.setType(c.getType());
                               } else if (parent.getParentNode().isPresent()) {
                                   Node p = parent.getParentNode().get();
                                   if (p instanceof ExpressionStmt) {
                                       if (p.getParentNode().isPresent()) {
                                           int index=Utils.getIndex(p);
                                           //int index = p.getParentNode().get().getChildNodes().indexOf(p);
                                           List<VariableDeclarator> remove = new ArrayList<>();
                                           for (VariableDeclarator var : ((VariableDeclarationExpr) parent).getVariables()) {
                                               if (var.equals(b)) {
                                                   VariableDeclarator newVar=var.clone();
                                                   Utils.insertStmt((BlockStmt) p.getParentNode().get(),
                                                           new ExpressionStmt(new VariableDeclarationExpr(newVar)), index);
                                                   newVar.setType(c.getType());
                                                   remove.add(var);
                                                   buggyVariableBuilder.updateV2V(entry.getKey(), newVar);
                                               }
                                           }
                                           ((VariableDeclarationExpr) parent).getVariables().removeAll(remove);
                                       }
                                   }
                               }

                           }
                       }
                   //}
               }
           }
            System.out.println(entry.getKey().getName()+" match "+entry.getValue().getName());
            addPreMatch(entry.getKey().getName(), buggyVariableBuilder.getScopeIndex(entry.getKey().getScope()),
                    entry.getValue().getName(), correctVariableBuilder.getScopeIndex(entry.getValue().getScope()));
        }
    }

    public void isDEAorIsDUA(MethodBuilder buggyM){
        N=correctVariableBuilder.getAllSize();
        M=buggyVariableBuilder.getAllSize();
        map=new boolean[M][N];
        vis = new boolean[N];
        p = new int[N];

        for (int i = 0; i < N; i++) {
            vis[i] = false;
            p[i] = -1;
        }

        for(int i=0;i<M;i++){
            Variable b=buggyVariableBuilder.getVariableList().get(i);
            for(int j=0;j<N;j++){
                Variable c=correctVariableBuilder.getVariableList().get(j);
                if(UnMatchC.contains(c)&&UnMatchB.contains(b)){
                    if(Variable.isTypeEqual(b.getType(),c.getType())
                            &&buggyVariableBuilder.getScopeIndex(b.getScope()).equals(correctVariableBuilder.getScopeIndex(c.getScope()))
                            &&c.specialType.equals(b.specialType)
                    )
                        map[i][j]= (c.isDEA(b)||(calculateDefUseRate(b,c,buggyM)>MINIMUM_DEF_USE_MATCH_RATE));
                }
            }
        }
        Hungarian();
        putMapping();
    }

    public boolean canVariableMatch(Variable c,Variable b){
        return Variable.isTypeEqual(c.getType(),b.getType())
                //&&c.getDefine()>=b.getDefine()
                &&c.specialType.equals(b.specialType);
    }
    public void matchVariablesByRely(){
        for(Variable v:UnMatchC){
            int i=correctVariableBuilder.getIndex(v);
            CUnMatchRely.put(i,correctVariableBuilder.getRely(i));
            CUnMatchRelied.put(i,correctVariableBuilder.getBeRelied(i));
        }
        for(Variable v:UnMatchB){
            int i=buggyVariableBuilder.getIndex(v);
            BUnMatchRely.put(i,buggyVariableBuilder.getRely(i));
            BUnMatchRelied.put(i,buggyVariableBuilder.getBeRelied(i));
        }
        List<Variable>cRemove=new ArrayList<>();
        for(Variable vi:UnMatchC){
            List<Variable>bRemove=new ArrayList<>();
            int maxMatch=0;
            double useRate = 0.0;
            Variable matchB = null;
            for(Variable vj:UnMatchB){
                if(canVariableMatch(vi,vj)){
                    int i=correctVariableBuilder.getIndex(vi);
                    int j=buggyVariableBuilder.getIndex(vj);
                    int relyWeight=getWeight(CUnMatchRely.get(i),BUnMatchRely.get(j));
                    int reliedWeight=getWeight(CUnMatchRelied.get(i),BUnMatchRelied.get(j));
                    if((relyWeight+reliedWeight)>maxMatch){
                        maxMatch=reliedWeight+relyWeight;
                        matchB=vj;
                        useRate = calculateDefUseRate(vj,vi,buggyM);
                    }else if(relyWeight+reliedWeight == maxMatch){
                        double useRate2 = calculateDefUseRate(vj,vi,buggyM);
                        if(useRate2>useRate){
                            maxMatch=reliedWeight+relyWeight;
                            matchB=vj;
                            useRate = useRate2;
                        }
                    }
                }
            }
            if(matchB!=null){
                c2bMatch.put(vi,matchB);
                b2cMatch.put(matchB,vi);
                bRemove.add(matchB);
                cRemove.add(vi);
            }
            UnMatchB.removeAll(bRemove);
        }
        UnMatchC.removeAll(cRemove);
    }

    public void matchVariablesByDEA(){
        List<Variable>bRemove=new ArrayList<>();
        for(Variable buggyV:UnMatchB) {
            ArrayList<Variable> matchedCVars = new ArrayList<>();
            for(Variable correctV:UnMatchC){
                if(isDEA(buggyV, correctV) && isDEA(correctV, buggyV)
                && canVariableMatch(buggyV,correctV)){
                    matchedCVars.add(correctV);
                }
            }
            if(matchedCVars.size()>0){
                bRemove.add(buggyV);
                UnMatchC.remove(matchedCVars.get(0));
                b2cMatch.put(buggyV, matchedCVars.get(0));
                c2bMatch.put(matchedCVars.get(0), buggyV);
            }
        }
        UnMatchB.removeAll(bRemove);
    }

    public void matchVariablesByDUA(){
        List<Variable>bRemove=new ArrayList<>();
        for(Variable buggyV:UnMatchB) {
            ArrayList<Variable> matchedCVars = new ArrayList<>();
            for(Variable correctV:UnMatchC){
                if(canVariableMatch(correctV,buggyV)
                        &&isDUA(buggyV, correctV)){
                    matchedCVars.add(correctV);
                }
            }
            if(matchedCVars.size()>0){
                bRemove.add(buggyV);
                UnMatchC.remove(matchedCVars.get(0));
                b2cMatch.put(buggyV, matchedCVars.get(0));
                c2bMatch.put(matchedCVars.get(0), buggyV);
            }
        }
        UnMatchB.removeAll(bRemove);
    }

    public void matchVariablesByDUA(MethodBuilder methodBuilder,double minMatchRate){
        List<Variable>bRemove=new ArrayList<>();
        for(Variable buggyV:UnMatchB) {
            Variable matchedCVar = null;
            double matchRate = 0;
            for(Variable correctV:UnMatchC){
                double mr = 0;
                if(canVariableMatch(correctV,buggyV))
                    mr = calculateDefUseRate(buggyV, correctV, methodBuilder);
                if(mr >= minMatchRate && mr > matchRate){
                    matchRate = mr;
                    matchedCVar = correctV;
                }
            }
            if(matchedCVar!=null){
                bRemove.add(buggyV);
                UnMatchC.remove(matchedCVar);
                b2cMatch.put(buggyV,matchedCVar);
                c2bMatch.put(matchedCVar, buggyV);
            }
        }
        UnMatchB.removeAll(bRemove);
    }


    public boolean isDUA(Variable var1, Variable var2){
        return var1.getUses().equals(var2.getUses());
    }


    public boolean isDEA(Variable var1, Variable var2){
        int nullNum = 0;
        for(int i:var1.getUses()){
            if(var1.getValues().get(i) == null){
                if(i>=var2.getValues().size()||var2.getValues().get(i)!=null)
                    return false;
                else {
                    nullNum += 1;
                    continue;
                }
            }
            if (var2.getValues().get(i) == null) {
                return false;
            }
            if(!var1.getValues().get(i).equals(var2.getValues().get(i))){
                return false;
            }
        }
        return nullNum != var1.getUses().size();
    }

    public double calculateDefUseRate(Variable var1, Variable var2, MethodBuilder methodBuilder){
        int matchMark =0;
        if(var1.getDefine() == var2.getDefine()){
            matchMark += methodBuilder.getMetaBlockNodes().get(var1.getDefine()).getBlockMark();
        }
        for(int use:var1.getUses()){
            if(var2.getUses().contains(use))
                matchMark += methodBuilder.getMetaBlockNodes().get(use).getBlockMark();
        }
        for (int use: var1.getReturnUses()){
            if(var2.getReturnUses().contains(use))
                matchMark += methodBuilder.getMetaBlockNodes().get(use).getJumpBlock().getBlockMark();
        }
        return ((double)matchMark/((double) Math.max(var1.getDefUseMark(), var2.getDefUseMark())));
    }

    public static void addPreMatch(String buggyName,int buggyScope,String correctName,int correctScope){
        preMatch.put(new Pair<>(buggyName,buggyScope),new Pair<>(correctName,correctScope));
    }
    public static void removePreMatch(String buggyName,int buggyScope)
    {
        preMatch.remove(new Pair<>(buggyName,buggyScope));
    }
    public static void clearPreMatch(){
        preMatch.clear();
    }
    public int getWeight(List<Integer>cList,List<Integer>bList){
        int weight=0;
        for(int i:cList){
            Variable vi=correctVariableBuilder.getVariableList().get(i);
            for(int j:bList){
                Variable vj=buggyVariableBuilder.getVariableList().get(j);
                if(c2bMatch.containsKey(vi)&&c2bMatch.get(vi)!=null&& c2bMatch.get(vi).equals(vj)){
                    weight++;
                }
            }
        }
        return weight;
    }

    public boolean match(int i){
        for(int j=0;j<N;j++){
            if(map[i][j]&&!vis[j]){
                vis[j] = true;
                if(p[j]==-1 || match(p[j])){
                    p[j] = i;
                    return true;
                }
            }
        }
        return false;
    }
    public int Hungarian(){
        int cnt = 0;
        for (int i = 0; i < M; i++) {
            Arrays.fill(vis,Boolean.FALSE);
            cnt += match(i)?1:0;
        }
        return cnt;
    }

    public void putMapping(){
        for (int i = 0; i < N; i++) {
            if(p[i]!=-1){
                Variable c=correctVariableBuilder.getVariableList().get(i);
                Variable b=buggyVariableBuilder.getVariableList().get(p[i]);
                c2bMatch.put(c,b);
                b2cMatch.put(b,c);
                UnMatchC.remove(c);
                UnMatchB.remove(b);
            }
        }
    }
    public void addMatch(Variable c,Variable b){
        c2bMatch.put(c,b);
        b2cMatch.put(b,c);
    }
    public HashMap<Variable, Variable> getC2bMatch() {
        return c2bMatch;
    }
    public HashMap<Variable, Variable> getB2cMatch() {
        return b2cMatch;
    }

    public VariableBuilder getCorrectVariableBuilder(){return correctVariableBuilder;}
    public VariableBuilder getBuggyVariableBuilder(){return  buggyVariableBuilder;}
}
