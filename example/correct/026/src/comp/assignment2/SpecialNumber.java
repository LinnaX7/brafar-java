package comp.assignment2;

import java.util.Scanner;

public class SpecialNumber {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a positive number: ");
        int x = scanner.nextInt();
        boolean flag = isSpecial(x);
        if (flag) {
            System.out.println("The number is special");
        } else {
            System.out.println("The number is not special");
        }
    }

    public static boolean checkprimenumber(int x) {
        int flag = 0;
        for (int i = 2; i <= x / 2; ++i) {
            if (x % i == 0) {
                flag = 1;
                break;
            }
        }
        if (flag == 0)
            return true;
        else
            return false;
    }

    public static int returnint(int x, int y) {
        int u = 0;
        int result = 0;
        boolean flag = true;
        for (int i = 1; i <= y; i++) {
            u = x * i;
            if (u == y) {
                result = i;
                break;
            } else {
                result = 999;
            }
        }
        return result;
    }

    public static boolean isSpecial(int x) {
        boolean flag = false;
        int[] store = new int[3];
        for (int i = 2; i <= x; i++) {
            if (!flag) {
                int a = i;
                int b = returnint(i, x);
                if (checkprimenumber(a)) {
                    if (checkprimenumber(b)) {
                        flag = false;
                    } else {
                        for (int j = 1; j <= b; j++) {
                            if (!flag) {
                                int c = j;
                                int d = returnint(j, b);
                                if (checkprimenumber(c)) {
                                    if (checkprimenumber(d)) {
                                        flag = true;
                                        store[0] = a;
                                        store[1] = c;
                                        store[2] = d;
                                    }
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        boolean flag2 = true;
        if (!flag) {
            return false;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 3; j++) {
                if (store[i] == store[j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
