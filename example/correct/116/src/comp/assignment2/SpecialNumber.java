package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // let an array to store 10 prime numbers
        int[] primeArray = new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29 };
        // let an array to store count of prime factors of num
        int[] primeCount = new int[10];
        while (num > 1) {
            for (int j = 0; j < 10; j++) {
                // loop through prime numbers
                if (num % primeArray[j] == 0) {
                    // count of that prime factor + 1
                    primeCount[j]++;
                    num /= primeArray[j];
                }
            }
        }
        int count = 0;
        for (int j = 0; j < 10; j++) {
            if (primeCount[j] != 0) {
                count++;
            }
        }
        if (count == 3) {
            // if num has exactly 3 different prime factors
            // it is special.
            return true;
        }
        // otherwise, it is not special.
        return false;
    }
}
