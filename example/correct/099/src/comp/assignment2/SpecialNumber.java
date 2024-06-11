package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int counter = 0;
        int j = 2;
        int half = num / 2;
        while (num > 1 && j <= half) {
            if (num % j == 0) {
                counter += 1;
                while (num % j == 0) {
                    num = num / j;
                }
            }
            j++;
        }
        return (counter == 3) ? true : false;
    }
}
