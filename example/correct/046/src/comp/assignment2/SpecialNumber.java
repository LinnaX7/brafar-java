package comp.assignment2;

import java.util.ArrayList;

public class SpecialNumber {

    public static void main(String[] args) {
        isSpecial(210);
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        ArrayList<Integer> arrlist = new ArrayList<Integer>();
        int previous = 0;
        int divisor = 2;
        while (num >= 2) {
            if (num % divisor == 0) {
                if (previous != divisor) {
                    arrlist.add(divisor);
                }
                num = num / divisor;
                previous = divisor;
            } else {
                divisor++;
            }
        }
        if (arrlist.size() == 3) {
            return true;
        }
        return false;
    }
}
