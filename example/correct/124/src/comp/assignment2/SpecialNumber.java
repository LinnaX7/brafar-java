package comp.assignment2;

public class SpecialNumber {

    public static void main(String[] args) {
        System.out.println(isSpecial(60));
        System.out.println(isSpecial(210));
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int stk = 0;
        int div = 2;
        int a = num / 2;
        while (div <= a && num != 1) {
            if (num % div == 0) {
                stk++;
                while (num % div == 0) {
                    num = num / div;
                }
            }
            div++;
        }
        if (stk == 3) {
            return true;
        }
        return false;
    }
}
