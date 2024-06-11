package comp.assignment2;

import java.util.Scanner;

public class SpecialNumber {

    public static void main(String[] arg) {
        Scanner myObj = new Scanner(System.in);
        int x;
        boolean flag;
        System.out.println("Please input an integer:");
        x = myObj.nextInt();
        flag = isSpecial(x);
        if (flag == true)
            System.out.println("True");
        else
            System.out.println("False");
    }

    public static boolean isSpecial(int x) {
        int i, j, num = 0, count = 0;
        int[] Arr = new int[128];
        for (i = 2; i < x / 2 + 1; i++) {
            for (j = 2; j < x / 2 + 1; j++) {
                if (i % j == 0 && i != j) {
                    break;
                } else {
                    Arr[count] = i;
                    count++;
                    break;
                }
            }
        }
        for (i = 0; i < count - 1; i++) {
            if (x % Arr[i] == 0) {
                num++;
            }
        }
        if (num == 3) {
            return true;
        }
        return false;
    }
}
