package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int count = 0;
        for (int i = 2; i < num; i++) {
            boolean temp = false;
            while (num % i == 0) {
                temp = true;
                num = num / i;
            }
            if (temp) {
                count++;
            }
        }
        if (count == 2 && num != 0 || count == 3 && num == 0) {
            return true;
        }
        return false;
    }
}
