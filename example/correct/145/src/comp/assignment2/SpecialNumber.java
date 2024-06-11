package comp.assignment2;

import java.util.*;

public class SpecialNumber {

    public static boolean isSpecial(int i) {
        int count = 0;
        int mid = i / 2;
        int divisor = 2;
        while (i != 1 && divisor <= mid) {
            if (i % divisor == 0) {
                count++;
                while (i % divisor == 0) {
                    i = i / divisor;
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
        SpecialNumber s = new SpecialNumber();
        Scanner input = new Scanner(System.in);
        System.out.println("Please input a number:");
        int num = Integer.parseInt(input.nextLine());
        if (s.isSpecial(num))
            System.out.println(num + " is special");
        else
            System.out.println(num + " is not special");
    }
}
