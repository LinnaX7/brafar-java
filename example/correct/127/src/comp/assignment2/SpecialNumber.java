package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int c = 0;
        int factorNum = 0;
        int[] factorSet = new int[1];
        // Find the factor by given num;
        // Start from 2;
        for (int i = 2; i <= num; i++) {
            // Loop the given num can or can't divided by "i";
            while (num % i == 0) {
                // Exclude duplicate numbers;
                if (factorSet[factorNum] != i) {
                    factorSet[factorNum] = i;
                    c++;
                }
                num = num / i;
            }
        }
        return c == 3 ? true : false;
    }
}
