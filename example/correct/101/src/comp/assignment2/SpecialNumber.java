package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int number = num;
        // different prime factors
        int p = 0;
        for (int i = 2; i <= num; i++) {
            if (number % i == 0) {
                p = p + 1;
            }
            while (number % i == 0) {
                number = number / i;
            }
        }
        if (p == 3) {
            return true;
        }
        return false;
    }
}
