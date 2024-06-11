package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // initialization and declaration
        // show the what is the factor of number (num % remainder== 0 and factor has to be grater than 1)
        int factornum = 2;
        // how many prime number is divisible
        int primefactor = 0;
        while (num >= 2) {
            // as a definition of prime (greater than 1)
            if (num % factornum == 0) {
                // increase number of prime factor
                primefactor = primefactor + 1;
                while (num % factornum == 0) {
                    // to reduce the redundant factor
                    num = (num / factornum);
                }
                // to find another prime divisible num
                factornum = factornum + 1;
            } else {
                // to find another prime divisible num
                factornum = factornum + 1;
            }
        }
        if (primefactor == 3) {
            // only if it is divide with three different prime num
            return true;
        }
        return false;
    }
}
