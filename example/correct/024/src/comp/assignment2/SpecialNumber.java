package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        if (num <= 0) {
            return false;
        }
        int rt = (int) Math.sqrt(num);
        int count = 0;
        for (int i = 2; i <= rt; i++) {
            if (num % i == 0) {
                count += 1;
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
