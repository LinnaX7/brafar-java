package comp.assignment2;

import java.util.ArrayList;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        ArrayList divisor = new ArrayList();
        for (int i = 2; num >= 2; i++) {
            if (num % i == 0) {
                num = num / i;
                divisor.add(i);
                while (num % i == 0) {
                    num = num / i;
                }
            }
        }
        if (divisor.size() == 3) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(isSpecial(2431));
    }
}
