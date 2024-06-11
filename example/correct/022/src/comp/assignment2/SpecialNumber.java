package comp.assignment2;

// Ho Man Hin (20059357D)
public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // both 0 and -1 are not prime numbers
        if (num <= 0) {
            return false;
        }
        int i;
        int count = 0;
        int[] array = new int[5];
        while (num != 1) {
            i = 2;
            // Find the next factor
            while (num % i != 0) {
                i++;
            }
            // Check for repeats
            for (int j : array) {
                if (j == 0) {
                    break;
                } else {
                    if (i == j) {
                        return false;
                    }
                }
            }
            // Prepare for next loop
            num /= i;
            array[count] = i;
            count++;
            // Kill the loop if the count is too much
            // because of small array (to reduce runtime)
            if (count > 3) {
                return false;
            }
        }
        return count == 3;
    }
}
