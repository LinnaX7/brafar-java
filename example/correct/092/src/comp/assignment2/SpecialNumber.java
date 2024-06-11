package comp.assignment2;

import java.util.HashSet;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        HashSet<Integer> primeFactor = new HashSet<Integer>();
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                primeFactor.add(i);
                num /= i;
                i--;
            }
        }
        primeFactor.add(num);
        return primeFactor.size() == 3;
    }
}
