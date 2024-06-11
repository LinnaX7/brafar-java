package comp.assignment2;

import java.util.*;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int max = 100;
        int j = 0;
        int count = 0;
        boolean[] status = new boolean[max + 1];
        status[0] = true;
        status[1] = true;
        int[] primeNumbers = new int[25];
        for (int p = 2; p <= max; p++) {
            if (!status[p]) {
                for (int mulP = 2 * p; mulP <= max; mulP += p) {
                    status[mulP] = true;
                }
            }
        }
        for (int i = 0; i <= max; i++) {
            if (!status[i]) {
                primeNumbers[j] = i;
                j++;
            }
        }
        for (int index = 0; index < 25; index++) {
            if (num % primeNumbers[index] == 0 && num / primeNumbers[index] > 0) {
                num /= primeNumbers[index];
                count++;
            }
        }
        if (count == 3) {
            return true;
        }
        return false;
    }
}
