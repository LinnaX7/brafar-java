package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int i, count = 0, temp = num;
        boolean add = true;
        for (i = 2; i <= num - 1; i++) {
            while ((temp % i == 0) & isPrime(i)) {
                if (add) {
                    count++;
                    add = false;
                }
                temp = temp / i;
            }
            add = true;
        }
        if (count == 3) {
            return true;
        }
        return false;
    }

    public static boolean isPrime(int num) {
        // To test it whether a prime
        int i;
        boolean temp = true;
        for (i = 2; i <= num - 1; i++) {
            if (num % i == 0) {
                temp = false;
                break;
            }
        }
        return temp;
    }
}
