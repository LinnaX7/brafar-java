package comp.assignment2;

public class SpecialNumber {

    public static void main(String[] args) {
        for (int i = 0; i < 500; ++i) {
            if (isSpecial(i))
                System.out.printf("\n%d is special.\n", i);
        }
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int cnt = 0;
        boolean f = true;
        for (int i = 2; i < num / 2; ++i) {
            for (int j = 2; j < i / 2; ++j) {
                if (i % j == 0) {
                    f = false;
                    break;
                }
            }
            if (f && num % i == 0) {
                ++cnt;
            }
            f = true;
        }
        if (cnt == 3) {
            return true;
        }
        return false;
    }
}
