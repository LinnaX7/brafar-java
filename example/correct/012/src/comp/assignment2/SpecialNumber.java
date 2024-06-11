package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        if (num <= 0) {
            return false;
        }
        int pCount = 0;
        for (int i = 2; i <= num; i++) {
            if (num % i == 0) {
                pCount++;
                if (pCount > 3) {
                    break;
                }
                while (num % i == 0) {
                    num /= i;
                }
            }
        }
        return pCount == 3;
    }
}
