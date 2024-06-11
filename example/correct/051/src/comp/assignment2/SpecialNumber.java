package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 0;
        if (num == 0 || num == 1) {
            return false;
        }
        if (isprime(num) == true) {
            return false;
        }
        int x = num;
        for (int i = 2; i <= x; i++) {
            if (isprime(i) == true) {
                if (x % i == 0) {
                    count++;
                }
                if (count > 3) {
                    return false;
                }
                while (x % i == 0) {
                    x = x / i;
                }
            }
        }
        if (count == 3) {
            return true;
        }
        return false;
    }

    private static boolean isprime(int num) {
        if (num <= 1)
            return false;
        else {
            boolean flag = false;
            for (int i = 2; i <= num / 2; i++) {
                if (num % i == 0) {
                    flag = true;
                    break;
                }
            }
            if (!flag)
                return true;
            else
                return false;
        }
    }
}
