package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // count the number of prime factor
        int count = 0;
        int temp = num;
        for (int i = 2; i <= Math.sqrt((double) num); i++) {
            if (temp % i == 0) {
                count++;
            }
            while (temp % i == 0) {
                temp = temp / i;
            }
        }
        return count == 3;
    }
}
