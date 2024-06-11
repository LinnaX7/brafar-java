package comp.assignment2;

public class SpecialNumber {

    public static void main(String[] args) {
        System.out.println(isSpecial(-30));
    }

    // step2:whether it is prime factor
    public static boolean isPrime(int fac) {
        for (int i = 2; i <= fac / 2; ++i) {
            if (fac % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        boolean isSpecial = false;
        // step1:find factor
        int pf_count = 0;
        int[] prime_factor_arr;
        prime_factor_arr = new int[100];
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                if (isPrime(i) == true) {
                    pf_count++;
                }
            }
        }
        if (pf_count == 3) {
            return true;
        }
        return false;
    }
}
