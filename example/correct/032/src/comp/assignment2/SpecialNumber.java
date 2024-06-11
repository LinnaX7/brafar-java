package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int count = 0;
        for (int i = 2; i < (num / 2) + 1; i++) {
            if (num % i == 0) {
                count++;
            }
        }
        if (count == 6) {
            return true;
        }
        return false;
    }
}
