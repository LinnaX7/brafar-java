package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // Number of prime factors
        int numofPrime = 0;
        // Avoid duplicate prime number
        int prime = 1;
        // Label for finishing loop
        boolean stop = false;
        // Divide the number into prime factors
        while (num != 1) {
            stop = false;
            for (int a = 2; a <= num; a++) {
                if (isPrime(a) == true && num % a == 0 && prime != a) {
                    numofPrime++;
                    num /= a;
                    prime = a;
                    stop = true;
                    break;
                }
            }
            if (stop == false) {
                break;
            }
        }
        if (numofPrime == 3) {
            return true;
        }
        return false;
    }

    public static boolean isPrime(int number) {
        for (int a = 2; a < number; a++) {
            if (number % a == 0) {
                return false;
            }
        }
        return true;
    }
}
