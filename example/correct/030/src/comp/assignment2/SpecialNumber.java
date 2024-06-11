package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int s = 0;
        for (int i = 2; i < num; i++) {
            int k = 0;
            for (int j = 2; j < i; j++) {
                if (i % j == 0) {
                    k = 1;
                }
            }
            if (k != 1 & num % i == 0) {
                s = s + 1;
            }
        }
        if (s == 3) {
            return true;
        }
        return false;
    }
}
