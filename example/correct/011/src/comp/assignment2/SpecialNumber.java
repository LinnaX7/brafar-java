package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int pFactorCount = 0;
        for (int i = 2; i <= num / 2; i++) {
            // index from 2 to half of num(since the largest factor should be <= half of num except itself)
            if ((num % i == 0) && (isPrime(i))) {
                // if i is factor of num and i is a prime number, update count by adding 1
                pFactorCount++;
            }
        }
        if (pFactorCount == 3) {
            // If the count of prime factor = 3, it is special number
            return true;
        }
        // else, it is not.
        return false;
    }

    public static boolean isPrime(int num) {
        // method to check the input num is prime of not
        for (int i = 2; i <= num / 2; i++) {
            // index from 2 to half of num(since the largest factor should be <= half of num except itself)
            if ((num % i) == 0) {
                // check if num is divisible by i
                // if yes, it is not a prime number
                return false;
            }
            // return false(and break from the loop, also from the method)
        }
        // if the method have not return in the above loop, it means that the number is divisible by 1 and itself only,
        return true;
    }
    // which is a prime number.
}
