package variables;

public class ValueIndex{
    public int inValueIndex;
    public int outValueIndex;

    public ValueIndex(int inValueIndex, int outValueIndex){
        this.inValueIndex = inValueIndex;
        this.outValueIndex = outValueIndex;
    }

    public void setInValueIndex(int inValueIndex) {
        this.inValueIndex = inValueIndex;
    }

    public void setOutValueIndex(int outValueIndex) {
        this.outValueIndex = outValueIndex;
    }

    public int getInValueIndex() {
        return inValueIndex;
    }

    public int getOutValueIndex() {
        return outValueIndex;
    }
}
