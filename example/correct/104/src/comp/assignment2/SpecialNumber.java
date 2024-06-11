package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        // count the number of the prime factors of num//
        int x = 0;
        // the number by which num is going to be divided in the coming loop, namely the divisor//
        int y = 2;
        int half = num / 2;
        // loop to see how many prime factors of num are there//
        while (num != 1 && y <= half) {
            // every divisor of a number, except for that number itself, is at most half of the number//
            if (num % y == 0) {
                // if num is divisible by y, that means y is one of its factors//
                // increased by 1 whenever a prime factor occurs//
                x++;
                while (num % y == 0) {
                    // execute division//
                    num = num / y;
                }
            }
            // the divisor is increased by 1 to see if it can divide num again in the while loop//
            y++;
        }
        if (x == 3) {
            // if there are exactly three different prime factors of num, it is special//
            return true;
        }
        return false;
    }
}
