package cfs.guider.mapping;

import cfs.guider.CSNode;

import java.util.ArrayList;
import java.util.HashMap;

public class RoughMapping {
    private final CSNode src;
    private final CSNode dst;
    private final ArrayList<CSNode> srcNodes;
    private final ArrayList<CSNode> dstNodes;
    private final HashMap<CSNode, Integer> srcNodesIndexes;
    private final HashMap<CSNode, Integer> dstNodesIndexes;
    private int mappingScore;

    public RoughMapping(CSNode src, CSNode dst){
        this.src = src;
        this.dst = dst;
        this.srcNodes = new ArrayList<>();
        this.dstNodes = new ArrayList<>();
        srcNodesIndexes = new HashMap<>();
        dstNodesIndexes = new HashMap<>();
        mappingScore = 0;
    }

    public void mapping(){
        srcDFSInit(src);
        dstDFSInit(dst);
        this.mappingScore = roughMapping(this.srcNodes, this.dstNodes);
    }

    public HashMap<CSNode, Integer> getDstNodesIndexes() {
        return dstNodesIndexes;
    }

    public HashMap<CSNode, Integer> getSrcNodesIndexes() {
        return srcNodesIndexes;
    }

    public ArrayList<CSNode> getSrcNodes() {
        return srcNodes;
    }

    public ArrayList<CSNode> getDstNodes() {
        return dstNodes;
    }

    public double getMappingScoreRate() {
        if(Math.max(srcNodes.size(), dstNodes.size())==0){
            return 0;
        }
        return (Double)(mappingScore*1.0/Math.max(srcNodes.size(), dstNodes.size()));
    }

    public int getMappingScore(){
        return mappingScore;
    }

    public CSNode getSrc() {
        return src;
    }

    public CSNode getDst() {
        return dst;
    }

    public void setMappingScore(int mappingScore) {
        this.mappingScore = mappingScore;
    }

    public static int[][] mappingInit(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes){
        int M = srcNodes.size();
        int N = dstNodes.size();

        int[][] mappingScores = new int[M+1][N+1];
        for (int i = 0; i < M+1; i++) {
            mappingScores[i][0] = 0;
        }
        for (int i = 0; i < N+1; i++) {
            mappingScores[0][i] = 0;
        }
        return mappingScores;
    }

    public void srcDFSInit(CSNode node){
        this.srcNodesIndexes.put(node, this.srcNodes.size());
        this.srcNodes.add(node);
        for(CSNode child: node.getChildren()){
            srcDFSInit(child);
        }
    }

    public void dstDFSInit(CSNode node){
        this.dstNodesIndexes.put(node, this.dstNodes.size());
        this.dstNodes.add(node);
        for(CSNode child:node.getChildren()){
            dstDFSInit(child);
        }
    }

    public static int roughMapping(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes){
        int[][] mappingScores = mappingInit(srcNodes, dstNodes);
        for (int i = 1; i < srcNodes.size()+1; i++) {
            for (int j = 1; j < dstNodes.size()+1; j++) {
                boolean flag = isMatch(srcNodes.get(i-1), dstNodes.get(j-1));
                int inc = flag?1:0;
                mappingScores[i][j] = Math.max(mappingScores[i-1][j-1]+ inc,
                        Math.max(mappingScores[i-1][j], mappingScores[i][j-1]));
            }
        }
        return mappingScores[srcNodes.size()][dstNodes.size()];
    }

    public static boolean isMatch(CSNode node1, CSNode node2){
        return isTypeMatch(node1, node2) && isAncestorMatch(node1, node2);
    }

    public static boolean isTypeMatch(CSNode node1,CSNode node2){
        if(node1.getCsType() == node2.getCsType())
            return true;
        return node1.isBranch() && node2.isBranch();
    }


//    public boolean heightDiff(CSNode node1, CSNode node2){
//        return Math.abs(node1.getHeight() - node2.getHeight()) < MAX_HEIGHT;
//    }

    public static boolean isAncestorMatch(CSNode node1, CSNode node2){
        if(node1.getAncestorFW()==null && node2.getAncestorFW()!=null)
            return false;
        if(node2.getAncestorFW()==null && node1.getAncestorFW() != null){
            return false;
        }
        if(node1.getAncestorFW()==null && node2.getAncestorFW()==null){
            return true;
        }
        if(node1.getAncestorFW().size()!=node2.getAncestorFW().size()){
            return false;
        }
        for (int i = 0; i < node1.getAncestorFW().size(); i++) {
            if(node1.getAncestorFW().get(i).getCsType()!=node2.getAncestorFW().get(i).getCsType()){
                return false;
            }
        }
        return true;
    }
}
