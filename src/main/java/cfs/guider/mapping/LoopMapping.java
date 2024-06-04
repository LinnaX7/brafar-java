package cfs.guider.mapping;

import cfs.guider.CSNode;

public class LoopMapping extends RoughMapping{

    public LoopMapping(CSNode src, CSNode dst) {
        super(src, dst);
    }

    @Override
    public void srcDFSInit(CSNode node){
        if(node.isLoopNode()){
            this.getSrcNodes().add(node);
        }
        for(CSNode child: node.getChildren()){
            this.srcDFSInit(child);
        }
    }

    @Override
    public void dstDFSInit(CSNode node){
        if(node.isLoopNode()){
            this.getDstNodes().add(node);
        }
        for(CSNode child: node.getChildren()){
            this.dstDFSInit(child);
        }
    }

    @Override
    public void mapping(){
        srcDFSInit(this.getSrc());
        dstDFSInit(this.getDst());
        super.roughMapping(this.getSrcNodes(), this.getDstNodes());
    }

}
