package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int remainder = 0;
        int countfactors = 0;
        for (int i = 2; i <= num; i++) {
            remainder = num % i;
            if (remainder == 0) {
                num = num / i;
                countfactors = countfactors + 1;
            }
        }
        if (countfactors == 3) {
            return true;
        }
        return false;
    }
}
