
import java.util.*;
public class SpecialNumber{
    public static boolean isSpecial(int g){// It is for the number of prime divisors
        int count=0;// It is used to the value of divisor for next iteration of while loop
        int divisor=2;// It is the factors of a number are always <= half of the number (except the number itself)
        int mid=g/2;// we use while loop in this case in order to calculate number of unique prime factors
        while(g!=1 && divisor <= mid ){
            if(g%divisor==0){
                count++;
                while(g%divisor==0){
                    g=g/divisor;
                }
            }
            divisor++;
        }//if number of unique prime factors is exactly 3, go to true
        if(count == 3)
            return true;
        return false;
    }// use the function in order to check the code
    public static void main(String[] args){
        SpecialNumber s = new SpecialNumber();
        if(s.isSpecial(30))
            System.out.println("30 is special!");
        else
            System.out.println("30 is not special!");
        if(s.isSpecial(210))
            System.out.println("210 is special!");
        else
            System.out.println("210 is not special!");
    }
}


