package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int i = 0, j = 0, k = 0, flag = 0;
        for (i = 2; i <= num; i++) {
            if (num % i == 0) {
                flag = 1;
                for (j = 2; j <= i / 2; j++) {
                    if (i % j == 0) {
                        flag = 0;
                        break;
                    }
                }
                if (flag == 1) {
                    k += 1;
                }
            }
        }
        if (k == 3) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        if (isSpecial(60))
            System.out.println("True");
        else
            System.out.println("False");
    }
}
