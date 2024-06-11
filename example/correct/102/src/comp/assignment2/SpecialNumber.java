package comp.assignment2;

import java.util.Hashtable;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int factor = 2;
        Hashtable factorTable = new Hashtable<Integer, Boolean>();
        while (num > 0 && factor <= num) {
            if (factorTable.size() > 3) {
                return false;
            }
            if (num % factor == 0) {
                // is able to divide
                factorTable.put(factor, true);
                num = num / factor;
            } else {
                factor++;
            }
        }
        return factorTable.size() == 3;
    }
}
