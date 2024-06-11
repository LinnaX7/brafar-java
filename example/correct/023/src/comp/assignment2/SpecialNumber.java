package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int primeFactor = 0;
        boolean flag = false;
        for (int i = 2; i <= num; i++) {
            flag = false;
            if (num % i == 0) {
                flag = true;
                for (int j = 2; j <= i / 2; j++) {
                    if (i % j == 0) {
                        flag = false;
                    }
                }
            }
            if (flag) {
                primeFactor++;
            }
        }
        if (primeFactor != 3) {
            return false;
        }
        return true;
    }
}
