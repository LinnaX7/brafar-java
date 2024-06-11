package comp.assignment2;

/*
Tiago Teixeira Reis - 19085298d
 */
public class SpecialNumber {

    public static boolean isSpecial(int value) {
        int[] prime_numbers = new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97 };
        int[] unique_factors = new int[prime_numbers.length];
        for (boolean flag = true; value != 1 && value > 0; flag = true) {
            for (int x = 0; flag && x < prime_numbers.length; x++) {
                if (value % prime_numbers[x] == 0) {
                    unique_factors[x] = prime_numbers[x];
                    value /= prime_numbers[x];
                    flag = false;
                }
            }
        }
        if ((counter(unique_factors)) && value > 0) {
            return true;
        }
        return false;
    }

    static boolean counter(int[] unique) {
        int c = 0;
        for (int x = 0; x < unique.length; x++) {
            if (unique[x] != 0) {
                c++;
            }
        }
        if (c == 3) {
            return true;
        } else {
            return false;
        }
    }
}
