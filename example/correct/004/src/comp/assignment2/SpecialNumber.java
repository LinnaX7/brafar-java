package comp.assignment2;

import java.util.ArrayList;
import java.util.List;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        List<Integer> a = new ArrayList<>();
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (i == 2) {
                a.add(i);
            } else {
                boolean result = true;
                for (int k = 2; k < i; k++) {
                    if (i % k == 0) {
                        result = false;
                    }
                }
                if (result == true) {
                    a.add(i);
                }
            }
        }
        int number = 0;
        for (int i = 0; i < a.size(); i++) {
            if (num % a.get(i) == 0) {
                number++;
                num = num / a.get(i);
            }
        }
        if (number == 3) {
            return true;
        }
        return false;
        // Task 3: Return true if and only if 'num' is special
    }
}
