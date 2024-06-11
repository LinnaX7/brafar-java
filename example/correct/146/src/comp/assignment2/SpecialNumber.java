package comp.assignment2;

import java.util.ArrayList;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        if (num < 0) {
            // prime numbers can not be negative.
            return false;
        }
        int dividend = num;
        ArrayList<Integer> primFactors = new ArrayList<>();
        for (int p = 2; p < num; p++) {
            // check if the size of arraylist is over 3, if so, then break the loop;
            if (primFactors.size() > 3) {
                break;
            }
            // if the quotient is 0, then break the loop.
            if ((dividend / p) == 0) {
                break;
            }
            // need a check prime method???
            if ((num % p) == 0) {
                dividend = dividend / p;
                // add p into arraylist
                primFactors.add(p);
            }
            // else continue;
        }
        // check the size of arraylist, if equal to 3, return true, otherwise return false;
        if (primFactors.size() == 3) {
            return true;
        }
        return false;
    }
}
