package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 0;
        for (int i = 2; i < num; i++) {
            boolean flag = false;
            while (num % i == 0) {
                flag = true;
                num /= i;
            }
            if (flag) {
                count++;
            }
        }
        if (count == 3 && num == 0 || count == 2 && num != 0) {
            return true;
        }
        return false;
    }
}
