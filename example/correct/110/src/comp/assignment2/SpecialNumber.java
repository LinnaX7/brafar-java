package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int counter = 0;
        for (int j = 2; j < num; j++) {
            if (num % j == 0) {
                boolean isPrime = true;
                for (int i = 2; i < j; i++) {
                    if (j % i == 0) {
                        isPrime = false;
                        break;
                    }
                }
                if (isPrime == true) {
                    counter += 1;
                }
            }
        }
        // 1st for
        if (counter == 3) {
            return true;
        }
        return false;
    }
}
