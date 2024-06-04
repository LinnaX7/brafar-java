package program;

import java.util.ArrayList;

public class StackBuffer{
    public int beginIndex;
    public int endIndex;
    public int outIndex;
    public ArrayList<String> breakPointLineIndexes;

    public StackBuffer(int beginIndex, int endIndex, ArrayList<String> breakPointLineIndexes) {
        this.endIndex = endIndex;
        this.beginIndex = beginIndex;
        this.outIndex = -1;
        this.breakPointLineIndexes = breakPointLineIndexes;
    }

    public int getOriginalIndex(String line){
        if(beginIndex==-1||endIndex==-1){
            return -1;
        }
        int index = breakPointLineIndexes.subList(beginIndex, endIndex).indexOf(line);
        if(index==-1)
            return -1;
        else
            return beginIndex + index;
    }

    public void setOutIndex(int outIndex) {
        this.outIndex = outIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getNextOriginalIndex(String line){
        int index = breakPointLineIndexes.subList(beginIndex+1, endIndex).indexOf(line);
        if(index==-1)
            return -1;
        else
            return beginIndex + index +1;
    }

    public ArrayList<Integer> getAllIndexes(String line){
        ArrayList<Integer> allIndexes = new ArrayList<>();
        int index = breakPointLineIndexes.subList(beginIndex, endIndex).indexOf(line);

        int nextIndex = beginIndex+index;
        allIndexes.add(nextIndex);
        index = breakPointLineIndexes.subList(nextIndex+1,endIndex).indexOf(line);
        while (index != -1&&nextIndex+index +1!=endIndex-1){
            nextIndex = nextIndex+index +1;
            allIndexes.add(nextIndex);
            index = breakPointLineIndexes.subList(nextIndex+1,endIndex).indexOf(line);
        }
        return allIndexes;
    }

}