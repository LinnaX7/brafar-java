package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        if (countDistinct(findPrimeFactors(num)) == 3) {
            return true;
        }
        return false;
    }

    private static int countDistinct(int[] A) {
        int count = 0;
        for (int i = 0; i < A.length; i++) {
            int j = 0;
            for (j = 0; j < i; j++) {
                if (A[i] == A[j])
                    break;
            }
            if (j == i)
                count++;
        }
        return count;
    }

    private static int[] findPrimeFactors(int n) {
        int[] result = new int[0];
        int olength;
        for (int i = 2; i <= n; i++) {
            while (n % i == 0) {
                olength = result.length;
                int[] temp = new int[olength + 1];
                for (int j = 0; j < olength; j++) temp[j] = result[j];
                temp[olength] = i;
                result = temp;
                n /= i;
            }
        }
        return result;
    }
}
