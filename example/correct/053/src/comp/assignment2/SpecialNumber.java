package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int d = 2;
        int k = 0;
        int a = num / 2;
        while (d <= a && num != 1) {
            if (num % d == 0) {
                k++;
                while (num % d == 0) {
                    num = num / d;
                }
            }
            d++;
        }
        if (k == 3) {
            return true;
        }
        return false;
    }
}
