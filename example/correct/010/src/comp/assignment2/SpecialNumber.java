package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // count for prime number
        int count = 0;
        for (int i = 2; i <= num; i++) {
            if (num % i == 0) {
                count++;
            }
            while (num % i == 0) {
                num = num / i;
            }
        }
        // checking for special number
        if (count == 3) {
            return true;
        }
        return false;
    }
}
