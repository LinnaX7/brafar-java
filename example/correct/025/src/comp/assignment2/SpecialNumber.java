package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 0;
        for (int i = 1; i <= num; i++) {
            // Is a factor
            if (num % i == 0) {
                // The factor is a prime
                if (isPrime(i)) {
                    num /= i;
                    count++;
                }
                if ((num != 1) && (count == 3)) {
                    return false;
                }
                if ((num == 1) && (count == 3)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Check is prime number or not
    private static boolean isPrime(int num) {
        // 1 is not a prime number
        if (num == 1) {
            return false;
        }
        // 2 is a prime number
        if (num == 2) {
            return true;
        }
        // Other case
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
