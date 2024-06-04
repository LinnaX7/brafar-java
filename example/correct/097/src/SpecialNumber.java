import java.util.*;
public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count=0;
        int div=2;
        int m=num/2;
        while(num!=1 && div<= m){
            if(num%div==0){
                count++;
                while(num%div==0) {
                    num = num / div;
                }
            }
        div++;
        }
        if(count==3)
            return true;
        return false;
    }
    public static void main(String[] args){
        SpecialNumber sn= new SpecialNumber();
        if(sn.isSpecial(30))
            System.out.println("30 is special!");
        else
            System.out.println("30 is not special!");

        if(sn.isSpecial(210))
            System.out.println("210 is special!");
        else
            System.out.println("210 is not special!");


    }

}

