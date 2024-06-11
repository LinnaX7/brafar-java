package comp.assignment2;

import java.util.Scanner;

public class SpecialNumber {

    public static void main(String[] arg) {
        Scanner myObj = new Scanner(System.in);
        int target;
        boolean flag;
        System.out.println("Please input an integer:");
        target = myObj.nextInt();
        flag = isSpecial(target);
        if (flag == true)
            System.out.println("True");
        else
            System.out.println("False");
    }

    public static boolean isSpecial(int target) {
        int i, j, num = 0, count = 0;
        int[] primeArr = new int[128];
        for (i = 2; i < target / 2 + 1; i++) {
            for (j = 2; j < target / 2 + 1; j++) {
                if (i % j == 0 && i != j) {
                    break;
                } else {
                    primeArr[count] = i;
                    count++;
                    break;
                }
            }
        }
        for (i = 0; i < count - 1; i++) {
            if (target % primeArr[i] == 0) {
                num++;
            }
        }
        if (num == 3) {
            return true;
        }
        return false;
    }
}
