package comp.assignment2;

import java.util.*;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // use set so that the same prime factors number will be counted as 1
        Set<Integer> factors = new HashSet<>();
        for (int i = 2; i <= num; i++) {
            // i is the prime factors
            while (num % i == 0 && num > i) {
                num /= i;
                factors.add(i);
            }
            if (num == i) {
                factors.add(i);
                break;
            }
        }
        return factors.size() == 3;
    }
}
