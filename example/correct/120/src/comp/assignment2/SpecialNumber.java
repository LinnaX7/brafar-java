package comp.assignment2;

import java.util.Scanner;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int i, a = 0, b = 0;
        for (i = 2; i <= num; i++) {
            while (num != i) {
                if (num % i == 0) {
                    num = num / i;
                    if (i != a) {
                        b = b + 1;
                        a = i;
                    }
                    a = i;
                } else {
                    break;
                }
                ;
            }
        }
        if ((b + 1) == 3) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        int num;
        Scanner scan = new Scanner(System.in);
        num = scan.nextInt();
        System.out.println(isSpecial(num));
    }
}
