package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        return numOfFactors(num) == 3;
    }

    private static int numOfFactors(int num) {
        int count = 0;
        for (int divisor = 2; divisor <= num; divisor++) {
            if (num % divisor == 0) {
                count++;
            }
            while (num % divisor == 0) {
                num = num / divisor;
            }
        }
        return count;
    }
}
