package comp.assignment2;

import java.util.*;

public class SpecialNumber {

    static ArrayList<Integer> dividers = new ArrayList<Integer>(0);

    // constructors
    public SpecialNumber() {
    }

    public static void main(String[] args) {
        if (isSpecial(210))
            System.out.println("True");
        else
            System.out.println("False");
        System.out.println(Arrays.toString(dividers.toArray()));
    }

    public static boolean isSpecial(int n) {
        // check how many dividers
        // check the divider is prime
        // check how many dividers
        if (n % 2 == 0) {
            dividers.add(2);
        }
        for (int i = 2; i <= n; i++) {
            if (n % i == 0) {
                if (isPrime(i)) {
                    dividers.add(i);
                }
            }
        }
        if (dividers.size() != 3) {
            return false;
        }
        return true;
    }

    static boolean isPrime(int n) {
        if (n % 2 == 0)
            return false;
        for (int i = 3; i <= Math.sqrt(n); i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }
}
