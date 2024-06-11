package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // The idea of factorize in this method partially referenced from the Internet
        // Source: https://www.jb51.net/article/126008.htm
        if (num <= 1) {
            // <=1 is meaningless
            return false;
        }
        // initializing
        int number = num;
        boolean flagOfResult = true;
        int[] listOfFactors = new int[3];
        while (true) {
            boolean flagOfSame = false;
            boolean flagOfZero = false;
            int divider = getFactor(number);
            // the divider is a prime, check two conditions:
            // 1. is it a repeated factor
            for (int item : listOfFactors) {
                if (item == divider) {
                    flagOfSame = true;
                    break;
                }
            }
            // 2. is the three factors' requirement still not fulfilled
            if (flagOfSame == false) {
                for (int i = 0; i < 3; i++) {
                    if (listOfFactors[i] == 0) {
                        flagOfZero = true;
                        listOfFactors[i] = divider;
                        break;
                    }
                }
            }
            // if it is a new factor and exceed the three-factor limit, return false
            if ((flagOfZero == false) && (flagOfSame == false)) {
                flagOfResult = false;
                break;
            }
            // get the quotient
            int quotient = number / divider;
            if (isPrime(quotient)) {
                // if quotient is prime, this is the last factor.
                // check the two conditions for it
                flagOfSame = false;
                flagOfZero = false;
                for (int item : listOfFactors) {
                    if (item == quotient) {
                        flagOfSame = true;
                        break;
                    }
                }
                if (flagOfSame == false) {
                    for (int i = 0; i < 3; i++) {
                        if (listOfFactors[i] == 0) {
                            flagOfZero = true;
                            listOfFactors[i] = quotient;
                            break;
                        }
                    }
                }
                if ((flagOfZero == false) && (flagOfSame == false)) {
                    flagOfResult = false;
                }
                break;
            }
            // if quotient is not prime, decomposes it again.
            number = quotient;
        }
        for (int item : listOfFactors) {
            // whether exactly 3 unique factors
            if (item == 0) {
                flagOfResult = false;
            }
        }
        return flagOfResult;
    }

    public static boolean isPrime(int num) {
        // if the given value is a prime number, returns true; vice versa
        if (num == 2) {
            return true;
        }
        if ((num <= 1) || (num % 2 == 0)) {
            return false;
        }
        for (int i = 3; i < num; i += 2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int getFactor(int num) {
        // find the first factor in ascending oder
        int result = 0;
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                result = i;
                break;
            }
        }
        return result;
    }
}
