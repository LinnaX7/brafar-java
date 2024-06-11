package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int count = 0;
        for (int i = 0; i < num / 2; i++) {
            if (isPrime(i)) {
                if (num % i == 0) {
                    count++;
                }
            }
        }
        if (count == 3) {
            return true;
        }
        return false;
    }

    public static boolean isPrime(int number) {
        int flag = 0;
        if (number == 0 || number == 1) {
            return false;
        } else {
            for (int i = 2; i <= (number / 2); i++) {
                if (number % i == 0) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                return true;
            }
        }
        return false;
    }
}
