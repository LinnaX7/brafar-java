package sar.repair.utils;

public class Location implements Comparable<Location>{
    public final int block;
    public final int sequence; //within block sequence
    public Location(int block, int sequence) {
        this.block = block;
        this.sequence = sequence;
    }
    @Override
    public int compareTo(Location other) {
        int compare=Integer.compare(block,other.block);
        if(compare == 0){
            return Integer.compare(sequence, other.sequence);
        }else{
            return compare;
        }
    }
    public String toString() {
        return "("+block+", "+sequence+ ") : ";
    }
}
