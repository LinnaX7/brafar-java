package comp.assignment2;

// OOP_A1-2
// Made by Mike_Zhang(21098431d)
public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // store the result
        boolean check = true;
        // count the number of different prime factors
        int count = 0;
        // start from the least prime number
        int i = 2;
        final int NUM = num;
        while (i < NUM) {
            if (count > 3) {
                // if already counted 3 prime factors, break the loop for saving run time
                // mark it is NOT a Special number
                check = false;
                break;
            }
            if (isPrime(i)) {
                // check prime number
                if (num % i == 0) {
                    // check the factor
                    // if it is its factor, divided by i
                    num = num / i;
                    // System.out.println(i); // Test, print out the factor
                    // NO i++, loop again with the same i, check whether 'i' can still be the factor of the new num
                } else {
                    // it is not the factor
                    // move to next i
                    i++;
                    // current "i" is NOT the factor indicate the former 'i' is the factor, then count 1, also avoid the repeat counting
                    count++;
                    // as soon as the num becomes 1 (means no more factor), break the loop
                    if (num == 1) {
                        break;
                    }
                }
            } else // if not a prime number, move to the next number
            {
                i++;
            }
        }
        if (count != 3) {
            // if not exactly 3 different prime factors, mark 'false'
            check = false;
        }
        return check;
    }

    // checking the number is prime or not
    public static boolean isPrime(int num) {
        boolean check = false;
        if (num == 2) {
            check = true;
        } else {
            for (int i = 2; i < num; i++) {
                if (num % i == 0) {
                    check = false;
                    break;
                } else {
                    check = true;
                }
            }
        }
        return check;
    }
}
// OOP_A1-2
// Made by Mike_Zhang(21098431d)
