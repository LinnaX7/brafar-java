package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int i) {
        int count = 0;
        int divisor = 2;
        int mid = i / 2;
        for (; i != 1 && divisor <= mid; divisor += 1) {
            if (i % divisor == 0) {
                count += 1;
                for (; i % divisor == 0; i = i / divisor) {
                }
            }
        }
        if (count == 3) {
            return true;
        }
        return false;
    }
    // Test the code
}
