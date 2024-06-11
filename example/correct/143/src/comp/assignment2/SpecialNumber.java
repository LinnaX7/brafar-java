package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 1;
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                count++;
                while (num % i == 0) {
                    num /= i;
                }
            }
        }
        if (count == 3) {
            return true;
        }
        return false;
    }
}
