package cfs.guider.mapping;

import cfs.guider.CSNode;
import org.checkerframework.checker.units.qual.A;

import java.util.*;

public class LegalMapping extends RoughMapping {
//    public static final int MAX_HEIGHT = 3;

    private final HashMap<CSNode, CSNode> srcToDst;
    private final HashMap<CSNode, CSNode> dstToSrc;
    private final Set<Integer> srcCSNodesIndexes;
    private final Set<Integer> dstCSNodesIndexes;
    private final ArrayList<CSNode> srcCSNodes;
    private final ArrayList<CSNode> dstCSNodes;
    private int mappingFWScore;
    private double mapRate;
    private boolean isOptimal;

    public LegalMapping(CSNode src, CSNode dst, boolean isOptimal) {
        super(src, dst);
        srcToDst = new HashMap<>();
        dstToSrc = new HashMap<>();
        srcCSNodesIndexes = new TreeSet<>();
        dstCSNodesIndexes = new TreeSet<>();
        srcCSNodes = new ArrayList<>();
        dstCSNodes = new ArrayList<>();
        mapRate = 0.0;
        mappingFWScore = 0;
        this.isOptimal = isOptimal;
    }



    public void setMappingFWScore(int mappingFWScore) {
        this.mappingFWScore = mappingFWScore;
    }




    public ArrayList<CSNode> getSrcCSNodes() {
        return srcCSNodes;
    }

    public ArrayList<CSNode> getDstCSNodes() {
        return dstCSNodes;
    }


    public static class BestMatch{
        private String mappingPair;
        private int mappingFWScore;
        private int mappingScore;

        public void setMappingFWScore(int mappingFWScore) {
            this.mappingFWScore = mappingFWScore;
        }

        public void setMappingPair(String mappingPair) {
            this.mappingPair = mappingPair;
        }

        public void setMappingScore(int mappingScore) {
            this.mappingScore = mappingScore;
        }

        public int getMappingScore() {
            return mappingScore;
        }

        public int getMappingFWScore() {
            return mappingFWScore;
        }

        public String getMappingPair() {
            return mappingPair;
        }
    }

    public HashMap<CSNode, CSNode> getDstToSrc() {
        return dstToSrc;
    }

    public HashMap<CSNode, CSNode> getSrcToDst() {
        return srcToDst;
    }

    @Override
    public void mapping() {
        srcDFSInit(this.getSrc());
        dstDFSInit(this.getDst());
        initSrcCSNodes(this.getSrc());
        initDstCSNodes(this.getDst());
        updateCSNodesList();
        BestMatch bestMatch = legalMapping(this.srcCSNodes, this.dstCSNodes);
        fillMapping(this.srcCSNodes, this.dstCSNodes, bestMatch);
    }

    public static BestMatch legalMapping(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes){
        int[][] mappingScores = RoughMapping.mappingInit(srcNodes, dstNodes);
        ArrayList<ArrayList<Set<String>>> mappingPairs = mappingPairsInit(srcNodes, dstNodes);
        legalMapping(srcNodes, dstNodes, mappingPairs, mappingScores);
        return findBestMatch(srcNodes, mappingPairs.get(srcNodes.size()).get(dstNodes.size()));
    }

    private void initSrcCSNodes(CSNode node){
        if(!isOptimal) {
            for (int i = 0; i < this.getSrcNodesIndexes().size(); i++) {
                srcCSNodesIndexes.add(i);
            }
            return;
        }
        if(node.getCsType()== CSNode.CSType.METHOD_DECLARATION)
            srcCSNodesIndexes.add(this.getSrcNodesIndexes().get(node));
          if(node.isLoopNode()){
              srcCSNodesIndexes.add(this.getSrcNodesIndexes().get(node));
              for(CSNode anc:node.getAncestors()){
                  if(srcCSNodesIndexes.contains(this.getSrcNodesIndexes().get(anc)))
                      break;
                  srcCSNodesIndexes.add(this.getSrcNodesIndexes().get(anc));
                  if(anc.getCsType()== CSNode.CSType.IF_STMT){
                      for(CSNode child:anc.getChildren()){
                          srcCSNodesIndexes.add(this.getSrcNodesIndexes().get(child));
                      }
                  }
              }
          }
          for(CSNode child:node.getChildren())
              initSrcCSNodes(child);
    }

    private void updateCSNodesList(){
        for(int i:srcCSNodesIndexes){
          srcCSNodes.add(this.getSrcNodes().get(i));
        }
        for (int i:dstCSNodesIndexes) {
            dstCSNodes.add(this.getDstNodes().get(i));
        }

    }

    private void initDstCSNodes(CSNode node){
        if(!isOptimal) {
            for (int i = 0; i < this.getDstNodesIndexes().size(); i++) {
                dstCSNodesIndexes.add(i);
            }
            return;
        }
        if(node.getCsType()== CSNode.CSType.METHOD_DECLARATION)
            dstCSNodesIndexes.add(this.getDstNodesIndexes().get(node));
        if(node.isLoopNode()){
          dstCSNodesIndexes.add(this.getDstNodesIndexes().get(node));
          for(CSNode anc:node.getAncestors()){
              if(dstCSNodesIndexes.contains(this.getDstNodesIndexes().get(anc)))
                  break;
              dstCSNodesIndexes.add(this.getDstNodesIndexes().get(anc));
              if(anc.getCsType() == CSNode.CSType.IF_STMT){
                  for (CSNode child:anc.getChildren()){
                      dstCSNodesIndexes.add(this.getDstNodesIndexes().get(child));
                  }
              }
          }
        }
        for(CSNode child:node.getChildren())
          initDstCSNodes(child);
    }

    static class MapsComparator implements Comparator<String> {
        public int compare(String maps1, String maps2) {
            if (maps1.length() > maps2.length())
                return 1;
            else if (maps1.length() < maps2.length())
                return -1;
            return maps1.compareTo(maps2);
        }
    }

    public static ArrayList<ArrayList<Set<String>>> mappingPairsInit(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes) {
        ArrayList<ArrayList<Set<String>>> mappingPairs = new ArrayList<>();
        for (int i = 0; i < srcNodes.size() + 1; i++) {
            Set<String> Zero = new HashSet<>();
            Zero.add("");
            ArrayList<Set<String>> col = new ArrayList<>();
            col.add(Zero);
            mappingPairs.add(col);
        }
        for (int i = 0; i < srcNodes.size()+1; i++) {
            for (int j = 1; j < dstNodes.size()+1; j++) {
                Set<String> Zero = new HashSet<>();
                Zero.add("");
                mappingPairs.get(i).add(Zero);
            }
        }
        return mappingPairs;
    }

    public boolean isBranchCrossMatch() {
        for (Map.Entry<CSNode, CSNode> entry : srcToDst.entrySet()) {
            CSNode src = entry.getKey();
            CSNode dst = entry.getValue();
            if (src.getCsType() == CSNode.CSType.ELSE_BRANCH && dst.getCsType() == CSNode.CSType.THEN_BRANCH)
                return true;
            if (dst.getCsType() == CSNode.CSType.ELSE_BRANCH && src.getCsType() == CSNode.CSType.THEN_BRANCH)
                return true;
        }
        return false;
    }

    private static boolean satisfyLegalCondition1(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes, CSNode node1, CSNode node2, String maps) {
        if (node1.isBranch()) {
            int n1pI = srcNodes.indexOf(node1.getParent());
            int n2pI = dstNodes.indexOf(node2.getParent());
            if(maps.length()>0 && Integer.parseInt(maps.substring(0,maps.indexOf(",")))==n1pI)
                return maps.contains(String.format("%d,%d;", n1pI, n2pI));
            return maps.contains(String.format(";%d,%d;", n1pI, n2pI));
        }
        return true;
    }

    private static BestMatch findBestMatch(ArrayList<CSNode> srcNodes, Set<String> mappingPairs) {
        int maxFW = -1;
        int maxPair = -1;
        Comparator<String> comp = new MapsComparator();
        TreeSet<String> ts = new TreeSet<>(comp);
        ts.addAll(mappingPairs);
        BestMatch bestMatch = new BestMatch();

        for (String maps : ts) {
            int count = 0;
            int countFW = 0;
            boolean flag = false;
            if(maps.length()>0) {
                for (String pair : maps.substring(0, maps.lastIndexOf(";")).split(";")) {
                    count += 1;
                    int i = Integer.parseInt(pair.split(",")[0]);
                    if (srcNodes.get(i).isLoopNode()) {
                        countFW += 1;
                    }
                }
            }
            if (countFW > maxFW) {
                flag = true;
            } else if (countFW == maxFW && count > maxPair) {
                flag = true;
            }
            if (flag) {
                maxFW = countFW;
                maxPair = count;
                bestMatch.setMappingPair(maps);
            }
        }
        bestMatch.setMappingScore(maxPair);
        bestMatch.setMappingFWScore(maxFW);
        return bestMatch;
    }

    public double getMapRate() {
        this.mapRate = (double) this.getMappingScore()/(double) Math.max(this.getSrcNodes().size(),this.getDstNodes().size());
        return this.mapRate;
    }

    public static HashMap<CSNode,CSNode> getMatchFromString(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes, BestMatch bestMatch) {
        HashMap<CSNode,CSNode>srcToDst = new HashMap<>();
        if(bestMatch.getMappingPair().length()>0) {
            for (String pair : bestMatch.getMappingPair().substring(0, bestMatch.getMappingPair().length() - 1).split(";")) {
                int i = Integer.parseInt(pair.split(",")[0]);
                int j = Integer.parseInt(pair.split(",")[1]);
                srcToDst.put(srcNodes.get(i), dstNodes.get(j));
            }
        }
        return srcToDst;
    }

    public void setAncestorMatch(CSNode node){
        if(node.getParent()!=null && !node.getParent().isHasDBeMatched()){
            node.getParent().setHasDBeMatched(true);
            setAncestorMatch(node.getParent());
        }
    }

    public void fillMapping(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes, BestMatch bestMatch){
        if(bestMatch.getMappingPair().length()>0) {
            String[] bestMatches = bestMatch.getMappingPair().substring(0, bestMatch.getMappingPair().length() - 1).split(";");
            for(int s = bestMatches.length-1;s>=0;s--){
                int i = Integer.parseInt(bestMatches[s].split(",")[0]);
                int j = Integer.parseInt(bestMatches[s].split(",")[1]);
                this.srcToDst.put(srcNodes.get(i), dstNodes.get(j));
                this.dstToSrc.put(dstNodes.get(j), srcNodes.get(i));
                setAncestorMatch(srcNodes.get(i));
                setAncestorMatch(dstNodes.get(j));
            }

        }
        this.setMappingScore(this.getMappingScore() + bestMatch.getMappingScore());
        this.setMappingFWScore(this.mappingFWScore+ bestMatch.getMappingFWScore());
    }

    public void fillMapping(HashMap<CSNode,CSNode> srcToDst, BestMatch bestMatch){
        for (Map.Entry<CSNode, CSNode> entry : srcToDst.entrySet()) {
            CSNode src = entry.getKey();
            CSNode dst = entry.getValue();
            this.srcToDst.put(src, dst);
            this.dstToSrc.put(dst, src);
        }
        this.setMappingScore(this.getMappingScore()+ bestMatch.getMappingScore());
        this.setMappingFWScore(this.mappingFWScore+ bestMatch.getMappingFWScore());
    }

    public static ArrayList<CSNode> getIfCSNodeToChangeBranch(HashMap<CSNode, CSNode> srcToDst){
        ArrayList<CSNode> ifNodesToBeChanged = new ArrayList<>();
        for (Map.Entry<CSNode, CSNode> entry : srcToDst.entrySet()) {
            CSNode src = entry.getKey();
            CSNode dst = entry.getValue();
            if (src.getCsType() == CSNode.CSType.ELSE_BRANCH && dst.getCsType() == CSNode.CSType.THEN_BRANCH){
                ifNodesToBeChanged.add(src.getParent());
            }
            else if (dst.getCsType() == CSNode.CSType.ELSE_BRANCH && src.getCsType() == CSNode.CSType.THEN_BRANCH) {
                ifNodesToBeChanged.add(src.getParent());
            }
        }
        return ifNodesToBeChanged;
    }


    private static boolean satisfyLegalCondition2(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes, CSNode node1, CSNode node2, String maps) {
        if (node1.getAncestors() != null) {
            for (CSNode anc1 : node1.getAncestors()) {
                int n1cI = srcNodes.indexOf(anc1);
                String ancStr = String.format("%d,", n1cI);
                if (!maps.startsWith(ancStr)) ancStr = String.format(";%d,",n1cI);
                int in = maps.indexOf(ancStr);
                if (in != -1) {
                    in = in + ancStr.length();
                    int ancMapIn = Integer.parseInt(maps.substring(in).split(";")[0]);
                    if (!node2.getAncestors().contains(dstNodes.get(ancMapIn)))
                        return false;
                }
            }
        }

        if (node2.getAncestors() != null) {
            for (CSNode anc2 : node2.getAncestors()) {
                int n2cI = dstNodes.indexOf(anc2);
                int in = maps.indexOf(String.format(",%d;", n2cI));
                if (in != -1) {
                    int ancMapIn = Integer.parseInt((maps.substring(0, in)).split(";")[maps.substring(0, in).split(";").length - 1]);
                    if (!node1.getAncestors().contains(srcNodes.get(ancMapIn)))
                        return false;
                }
            }
        }
        return true;
    }

    private static boolean satisfyLegalCondition3(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes, CSNode node1, CSNode node2, String maps) {
        if (node1.getAncestors() != null) {
            for (CSNode anc1 : node1.getAncestors()) {
                if (anc1.getCsType() == CSNode.CSType.ELSE_BRANCH) {
                    int n1cI = srcNodes.indexOf(anc1);
                    int in = maps.indexOf(String.format(";%d", n1cI));
                    if (in == -1) {
                        CSNode _then = anc1.getParent().getChildren().get(0);
                        int then_i = srcNodes.indexOf(_then);
                        for (int i = then_i; i < n1cI; i++) {
                            if (maps.contains(String.format(";%d,", i))||maps.startsWith(String.format("%d,",i)))
                                return false;
                        }
                    }
                }
            }
        }
        if (node2.getAncestors() != null) {
            for (CSNode anc2 : node2.getAncestors()) {
                if (anc2.getCsType() == CSNode.CSType.ELSE_BRANCH) {
                    int n2cI = dstNodes.indexOf(anc2);
                    int in = maps.indexOf(String.format(",%d;", n2cI));
                    if (in == -1) {
                        CSNode _then = anc2.getParent().getChildren().get(0);
                        int then_i = dstNodes.indexOf(_then);
                        for (int i = then_i; i < n2cI; i++) {
                            if (maps.contains(String.format(",%d;", i)))
                                return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean legalMatch(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes, CSNode node1, CSNode node2, String maps) {
        return satisfyLegalCondition1(srcNodes, dstNodes, node1, node2, maps) &&
                satisfyLegalCondition2(srcNodes, dstNodes, node1, node2, maps) &&
                satisfyLegalCondition3(srcNodes, dstNodes, node1, node2, maps);
    }


    public static void legalMapping(ArrayList<CSNode> srcNodes, ArrayList<CSNode> dstNodes, ArrayList<ArrayList<Set<String>>> mappingPairs, int[][] mappingScores) {
        for (int i = 1; i < srcNodes.size() + 1; i++) {
            for (int j = 1; j < dstNodes.size() + 1; j++) {
                boolean flag = isMatch(srcNodes.get(i - 1), dstNodes.get(j - 1));
                Set<String> mappingPairB = new TreeSet<>(mappingPairs.get(i - 1).get(j));
                Set<String> mappingPairA = new TreeSet<>(mappingPairs.get(i - 1).get(j - 1));
                Set<String> mappingPairC = new TreeSet<>(mappingPairs.get(i).get(j - 1));
                mappingPairB.removeAll(mappingPairA);
                mappingPairC.removeAll(mappingPairA);
                Set<String> mappingPairNow = new HashSet<>(mappingPairB);
                mappingPairNow.addAll(mappingPairC);

//                if((mappingPairB.size()>0&&dstNodes.get(j-1).isFindBestMap()) || mappingPairC.size()>0 &&srcNodes.get(i-1).isFindBestMap())
//                    flag = false;
                int inc = 0;
                if (flag) {
                    String pair = String.format("%d,%d;", i - 1, j - 1);
                    boolean flagIn = false;
                    for (String maps : mappingPairA) {
                        if (legalMatch(srcNodes, dstNodes, srcNodes.get(i - 1), dstNodes.get(j - 1), maps)) {
                            mappingPairNow.add(maps + pair);
                            flagIn = true;
                            if ((maps + pair).split(";").length > (mappingScores[i - 1][j - 1])) {
                                inc = 1;
                            }
                        } else if (mappingPairs.get(i - 1).get(j).contains(maps) &&
                                mappingPairs.get(i).get(j - 1).contains(maps)) {
                            mappingPairNow.add(maps);
                        }
                    }
                    if (!flagIn) {
                        int t_i = i - 1;
                        int t_j = j - 1;
                        while (!flagIn && t_i >= 1 && t_j >= 1) {
                            for (String maps : mappingPairs.get(t_i - 1).get(t_j - 1)) {
                                if (legalMatch(srcNodes, dstNodes, srcNodes.get(i - 1), dstNodes.get(j - 1), maps)) {
                                    mappingPairNow.add(maps + pair);
                                    flagIn = true;
                                }
                            }
                            t_i -= 1;
                            t_j -= 1;
                        }
                    }
                } else {
                    for (String maps : mappingPairs.get(i - 1).get(j - 1)) {
                        if (mappingPairs.get(i - 1).get(j).contains(maps) &&
                                mappingPairs.get(i).get(j - 1).contains(maps)) {
                            mappingPairNow.add(maps);
                        }
                    }
                }
                mappingPairs.get(i).get(j).remove("");
                mappingPairs.get(i).get(j).addAll(mappingPairNow);
                mappingScores[i][j] = Math.max(mappingScores[i - 1][j - 1] + inc,
                        Math.max(mappingScores[i - 1][j], mappingScores[i][j - 1]));
            }
        }
    }
}


//    public static void main(String[] args) throws IOException {
//        long startTime = System.currentTimeMillis();
//        String path = "data/Assignment06/Problem01/correct/001/src/hk/edu/polyu/comp/comp2021/assignment6/Search.java";
//        String path2 = "data/Assignment06/Problem01/wrong/001/src/hk/edu/polyu/comp/comp2021/assignment6/Search.java";
//        JavaParser javaParser=new JavaParser();
//        CompilationUnit compilationUnit=javaParser.parse(new File(path)).getResult().get();
//        Refactor.execRule(compilationUnit,13);
//        Refactor.execRule(compilationUnit,1);
//        CSNode csNode1 = new CSNode(compilationUnit.findAll(MethodDeclaration.class).get(0),0, CSNode.CSType.METHOD_DECLARATION,null);
//
//        JavaParser javaParser2=new JavaParser();
//        CompilationUnit compilationUnit2=javaParser2.parse(new File(path2)).getResult().get();
//
//        Refactor.execRule(compilationUnit2,13);
//        Refactor.execRule(compilationUnit2,1);
//        System.out.println(compilationUnit2);
//
//        CSNode csNode2 = new CSNode(compilationUnit2.findAll(MethodDeclaration.class).get(0),0, CSNode.CSType.METHOD_DECLARATION,null);
//
//        Mapping mapping = new Mapping(csNode1,csNode2);
//
//        EditScript editScript=new EditScript(mapping);
//        System.out.println(compilationUnit);
//        System.out.println(compilationUnit2);
//        long endTime = System.currentTimeMillis();
//        System.out.println("程序运行时间：" + (endTime - startTime) + "ms");
////        File file = new File(path);
//        if(!file.getParentFile().exists()){
//            file.getParentFile().mkdirs();
//        }
//        Writer out = new FileWriter(file);
//        out.write(String.valueOf(compilationUnit));
//        out.close();
//
//        File file2 = new File(path2);
//        if(!file2.getParentFile().exists()){
//            file2.getParentFile().mkdirs();
//        }
//        Writer out2 = new FileWriter(file2);
//        out2.write(String.valueOf(compilationUnit2));
//        out2.close();
//    }
//
//}
