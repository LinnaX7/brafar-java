package comp.assignment2;

public class SpecialNumber {

    // this is a method examining whether the factors are prime numbers
    public static boolean isPrime(int number) {
        // to calculate the factor of a number n, no need to go through 2~n-1, only need 2~n^(0.5)
        // the square root of num may be a fraction, but we can just keep the integer part
        int temp = (int) Math.sqrt(number);
        for (int i = 2; i <= temp; i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // to get all factors of num, only need to check the numbers equals or less than num^(0.5)
        // we get the factor smaller than num^(0.5), and we could calculate the other factor named anotherFctor
        int sqrtNum = (int) Math.sqrt(num);
        int anotherFactor;
        int primeFactor = 0;
        // only need to go through 2~num^(0.5)
        for (int i = 2; i <= sqrtNum; i++) {
            // check if i a factor of num
            if (num % i == 0) {
                anotherFactor = num / i;
                // check whether i is a prime number
                if (isPrime(i)) {
                    primeFactor++;
                }
                // check whether anotherFactor is a prime number
                if (isPrime(anotherFactor)) {
                    primeFactor++;
                }
            }
        }
        if (primeFactor == 3) {
            return true;
        }
        return false;
    }
}
