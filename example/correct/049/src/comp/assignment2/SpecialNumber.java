package comp.assignment2;

import java.util.ArrayList;
import java.util.HashSet;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int i, n;
        int[] array1 = new int[] {};
        ArrayList<Integer> m = new ArrayList<>();
        for (i = 2; i <= num; i++) {
            while (num != i) {
                if (num % i == 0) {
                    num = num / i;
                    m.add(i);
                } else {
                    break;
                }
            }
        }
        m.add(num);
        HashSet hs = new HashSet();
        hs.addAll(m);
        m.clear();
        m.addAll(hs);
        if (m.size() == 3) {
            return true;
        }
        return false;
    }
}
