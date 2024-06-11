package comp.assignment2;

import java.util.HashSet;
import java.util.Set;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // to aviod counting the same numbers twice
        Set<Integer> primeFactors = new HashSet<>();
        // 1 is not special
        if (num == 1) {
            return false;
        }
        for (int i = 2; i <= num; i++) {
            while (num % i == 0 && i != num) {
                // get prime factors
                num = num / i;
                primeFactors.add(i);
            }
            if (num == i) {
                primeFactors.add(i);
                break;
            }
        }
        if (primeFactors.size() == 3) {
            // decide whether it's special
            return true;
        }
        return false;
    }
}
