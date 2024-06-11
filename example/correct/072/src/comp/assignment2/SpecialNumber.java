package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        if (num < 2 * 3 * 5) {
            return false;
        }
        int i = 2;
        int primeCnt = 0;
        while (num > 1) {
            if (num % i == 0) {
                while (num % i == 0) {
                    num /= i;
                }
                primeCnt++;
            }
            i++;
        }
        return primeCnt == 3;
    }
}
