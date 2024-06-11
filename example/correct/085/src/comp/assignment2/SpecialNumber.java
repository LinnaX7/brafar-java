package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // initialize counter
        int counter = 0;
        for (int i = 2; i <= num; i++) {
            if (num % i == 0) {
                // if num can be divided by i
                // increment to counter
                counter++;
                while (num % i == 0) {
                    // while i can still be divided by current divider
                    // divide num by i
                    num = num / i;
                }
            }
        }
        if (counter == 3) {
            // if special counter meets 3
            // return true
            return true;
        }
        // special counter can not meet 3
        return false;
    }
}
