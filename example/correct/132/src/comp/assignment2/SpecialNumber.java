package comp.assignment2;

import java.util.*;

public class SpecialNumber {

    public static void main(String[] args) {
        System.out.println("Enter the number you want to check: ");
        Scanner sc = new Scanner(System.in);
        int inputx = sc.nextInt();
        if (isSpecial(inputx)) {
            System.out.println(inputx + " is a special number");
        } else {
            System.out.println(inputx + " is not a special number");
        }
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int[] arr = new int[100];
        int x = 0;
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                if (isprime(i) == true) {
                    arr[x] = i;
                    x += 1;
                }
            }
        }
        if (x == 3) {
            return true;
        }
        return false;
    }

    public static boolean isprime(int input) {
        boolean prime = true;
        if (input == 2 || input == 3) {
            prime = true;
        } else if (input > 2) {
            for (int i = 2; i <= input / 2; i++) {
                if (input % i == 0) {
                    prime = false;
                    break;
                }
            }
        }
        return prime;
    }
}
