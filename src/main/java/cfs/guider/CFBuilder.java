package cfs.guider;

import program.ProgramBuilder;

public class CFBuilder implements Comparable<CFBuilder>{
    ProgramBuilder programBuilder;
    CSNode csNode;
    int nodesCount;
    double loopPair;
    double roughPair;

    public CFBuilder(ProgramBuilder programBuilder, String methodName){
        this.programBuilder = programBuilder;
        csNode = new CSNode(programBuilder.getTargetMethodDeclaration(methodName));
    }

    public void setNodesCount(int nodesCount) {
        this.nodesCount = nodesCount;
    }

    public void setLoopPair(double loopPair) {
        this.loopPair = loopPair;
    }

    public void setRoughPair(double roughPair) {
        this.roughPair = roughPair;
    }

    public CSNode getCsNode() {
        return csNode;
    }

    public ProgramBuilder getProgramBuilder() {
        return programBuilder;
    }

    @Override
    public int compareTo(CFBuilder other) {
        if(this.loopPair>other.loopPair)
            return -1;
        else if(this.loopPair == other.loopPair){
            if(roughPair > other.roughPair)
                return -1;
            if(this.roughPair == other.roughPair) {
                if(this.nodesCount <= other.nodesCount)
                    return -1;
                else
                    return 1;
            }else{
                return 1;
            }
        }else
            return 1;
    }

}
