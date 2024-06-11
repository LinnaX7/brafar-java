package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int numPrime = 0, currPrime = 2;
        if (num >= 2) {
            while (currPrime < num && num > 1) {
                while (isPrime(currPrime) == false || num % currPrime != 0) {
                    currPrime++;
                }
                // test
                // System.out.println(currPrime);
                // increase #. of special number
                numPrime++;
                while (num % currPrime == 0) {
                    num /= currPrime;
                }
            }
            if (numPrime == 3) {
                // test
                // System.out.println("true");
                return true;
            }
            // test
            // System.out.println("false");
            // numPrime != 3
            return false;
        }
        // num < 2
        return false;
    }

    public static boolean isPrime(int aNum) {
        int factor = 2;
        while (factor < aNum) {
            if (aNum % factor == 0)
                return false;
            factor++;
        }
        return true;
    }
}
