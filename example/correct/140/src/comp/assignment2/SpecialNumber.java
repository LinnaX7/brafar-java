package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int n) {
        int number = 0;
        for (int i = 2; i <= (double) Math.sqrt(n); i++) {
            if (n % i == 0) {
                number++;
            }
            while (n % i == 0) {
                n = n / i;
            }
        }
        if (n > 1) {
            number++;
        }
        if (number == 3) {
            return true;
        }
        return false;
    }
}
