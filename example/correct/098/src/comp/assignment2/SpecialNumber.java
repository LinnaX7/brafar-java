package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int j = 2;
        int count = 0;
        while (j <= num && count < 3) {
            boolean flag = false;
            while (num % j == 0) {
                num /= j;
                flag = true;
            }
            if (flag) {
                count++;
            }
            j++;
        }
        return num == 1 && count == 3;
    }
}
