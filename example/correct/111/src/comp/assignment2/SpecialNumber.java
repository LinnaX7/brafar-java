package comp.assignment2;

import java.util.*;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        if (num < 30 || isPrime(num)) {
            return false;
        }
        // Task 3: Return true if and only if 'num' is special
        ArrayList<Integer> primeList = primeFactor(num);
        int len = primeList.size();
        for (int i = 0; i < len; i++) {
            for (int j = 1; j < len && j != i; j++) {
                for (int k = 2; k < len && k != j && k != i; k++) {
                    if (primeList.get(i) * primeList.get(j) * primeList.get(k) == num) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isPrime(int a) {
        for (int i = 2; i < a; i++) if (a % i == 0)
            return false;
        return true;
    }

    private static ArrayList<Integer> primeFactor(int a) {
        ArrayList<Integer> primes = new ArrayList<Integer>();
        for (int i = 2; i < a; i++) {
            if (a % i == 0 && isPrime(i)) {
                primes.add(i);
            }
        }
        return primes;
    }
}
