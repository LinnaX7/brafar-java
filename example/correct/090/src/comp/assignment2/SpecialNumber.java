package comp.assignment2;

import java.util.*;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        List<Integer> list = new ArrayList<>();
        int c = 1;
        for (int i = 2; i < num; i++) {
            while (num % i == 0) {
                int counter = 0;
                if (counter == 0) {
                    list.add(i);
                }
                counter = counter + 1;
                num = num / i;
            }
        }
        if (num > 2) {
            list.add(num);
        }
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) != list.get(i + 1)) {
                c = c + 1;
            }
        }
        if (c == 3) {
            return true;
        }
        return false;
    }
}
