package cfs.guider.mapping;
import cfs.guider.CSNode;
import java.util.ArrayList;
import java.util.HashMap;

public class LocalMapping extends RoughMapping{
    ArrayList<Integer> srcMappedIndexes;
    ArrayList<Integer> dstMappedIndexes;
    ArrayList<CSNode> allIfNodesToBeChanged;
    ArrayList<CSNode> srcIfNodes;
    ArrayList<CSNode> dstIfNodes;

    public LocalMapping(CSNode src, CSNode dst, LegalMapping legalMapping){
        super(src,dst);
        srcDFSInit(src);
        dstDFSInit(dst);
        srcMappedIndexes = new ArrayList<>();
        dstMappedIndexes = new ArrayList<>();
        allIfNodesToBeChanged = new ArrayList<>();
        srcIfNodes = new ArrayList<>();
        dstIfNodes = new ArrayList<>();
        localMapping(legalMapping);

    }

    public void localMappingInit(HashMap<CSNode, CSNode> srcToDst){
        for (int i = 0; i < this.getSrcNodes().size(); i++) {
            if(srcToDst.containsKey(this.getSrcNodes().get(i))){
                srcMappedIndexes.add(i);
                dstMappedIndexes.add(this.getDstNodesIndexes().get(srcToDst.get(this.getSrcNodes().get(i))));
            }
        }
    }

    public ArrayList<CSNode> getAllIfNodesToBeChanged() {
        return allIfNodesToBeChanged;
    }

    public void localMapping(ArrayList<CSNode> srcNodes, ArrayList<CSNode>dstNodes, LegalMapping legalMapping){
        if(srcNodes.size()>0||dstNodes.size()>0){
            LegalMapping.BestMatch bestMatch = LegalMapping.legalMapping(srcNodes, dstNodes);
            HashMap<CSNode, CSNode> src2Dst = LegalMapping.getMatchFromString(srcNodes, dstNodes, bestMatch);
            ArrayList<CSNode> ifNodesToBeChanged = LegalMapping.getIfCSNodeToChangeBranch(src2Dst);
            if(ifNodesToBeChanged.size() == 0) {
                srcIfNodes.addAll(srcNodes);
                dstIfNodes.addAll(dstNodes);
                legalMapping.fillMapping(src2Dst, bestMatch);
            }
            else
                allIfNodesToBeChanged.addAll(ifNodesToBeChanged);
        }
        srcNodes.clear();
        dstNodes.clear();
    }

    public void localMapping(LegalMapping legalMapping){
        localMappingInit(legalMapping.getSrcToDst());
        for (int i = 0; i < srcMappedIndexes.size(); i++) {
            int srcLastIndex = i==srcMappedIndexes.size()-1?this.getSrcNodes().size():srcMappedIndexes.get(i+1);
            int dstLastIndex = i==dstMappedIndexes.size()-1?this.getDstNodes().size():dstMappedIndexes.get(i+1);
            if(srcLastIndex-srcMappedIndexes.get(i)>1 || dstLastIndex-dstMappedIndexes.get(i)>1){
                int height = this.getSrcNodes().get(srcMappedIndexes.get(i)).getHeight();
                int j,k;
                ArrayList<CSNode> srcNodes = new ArrayList<>();
                ArrayList<CSNode> dstNodes = new ArrayList<>();
                for (j = srcMappedIndexes.get(i)+1,k = dstMappedIndexes.get(i)+1; j < srcLastIndex || k<dstLastIndex; ) {
                    if((j==srcLastIndex||this.getSrcNodes().get(j).getHeight()<=height) &&
                            (k==dstLastIndex||this.getDstNodes().get(k).getHeight()<=height)){
                        localMapping(srcNodes, dstNodes, legalMapping);
                        height = height -1;
                    }
                    if(j<srcLastIndex && this.getSrcNodes().get(j).getHeight()>height){
                        srcNodes.add(this.getSrcNodes().get(j));
                        j+=1;
                    }
                    if(k<dstLastIndex && this.getDstNodes().get(k).getHeight()>height){
                        dstNodes.add(this.getDstNodes().get(k));
                        k+=1;
                    }
                }
                localMapping(srcNodes, dstNodes, legalMapping);
            }
        }
    }
}
