package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 0;
        if (num < 30) {
            return false;
        }
        boolean[] listPrime = new boolean[num];
        for (int i = 2; i * i < num; i++) {
            if (!listPrime[i]) {
                for (int j = i * i; j < num; j += i) {
                    listPrime[j] = true;
                }
            }
        }
        for (int a = 2; a < listPrime.length; a++) {
            if (!listPrime[a]) {
                if (num % a == 0) {
                    count++;
                    while (num % a == 0) {
                        num /= a;
                    }
                    if (num == 1) {
                        return count == 3;
                    }
                }
            }
        }
        return false;
    }
}
