package comp.assignment2;

public class SpecialNumber {

    public static int minfactor(int x) {
        for (int i = 2; i < Math.sqrt(x) + 1; i++) {
            if (x % i == 0) {
                return i;
            }
        }
        return x;
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 0, a, m = num;
        while (m > 1) {
            a = minfactor(m);
            while (m % a == 0) {
                m /= a;
            }
            count++;
            if (count > 3) {
                return false;
            }
        }
        if (count == 3) {
            return true;
        }
        return false;
    }
}
