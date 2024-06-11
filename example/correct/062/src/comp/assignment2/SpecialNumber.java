package comp.assignment2;

import java.util.Scanner;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 0, divisor = 2, mid = num / 2;
        while (num != 1 && divisor <= mid) {
            if (num % divisor == 0) {
                count++;
                while (num % divisor == 0) {
                    num = num / divisor;
                }
            }
            divisor++;
        }
        if (count == 3) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.print("Enter An Integer : ");
        Scanner scan = new Scanner(System.in);
        SpecialNumber s = new SpecialNumber();
        int value = scan.nextInt();
        if (s.isSpecial(value))
            System.out.println(value + " is special. ");
        else
            System.out.println(value + " is not special. ");
    }
}
