package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 0;
        while (true) {
            boolean bool = false;
            for (int i = 2; i < num; i++) {
                if (num % i == 0) {
                    count++;
                    num = num / i;
                    bool = true;
                }
            }
            if (!bool) {
                count++;
                break;
            }
        }
        return count == 3;
    }
}
