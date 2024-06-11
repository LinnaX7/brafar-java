package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // an array to store different factors
        int[] prime_factors = new int[3];
        // initialize factor
        int divisor = 2;
        // record index in array
        int i = 0;
        // judge whether num is special number
        boolean is_num_Special = false;
        // check whether factor appear
        boolean next_index;
        if (num >= 2) {
            while (num != divisor && i < 2) {
                // at most 3 elements in array means special num needs only 3 prime factors
                if (num % divisor == 0) {
                    // a prime factor appear - trigger condition
                    // a factor appear
                    next_index = true;
                    if (next_index == true && prime_factors[0] == 0) {
                        // make sure first prime factor into array, next factor should evaluate through second if-condition
                        prime_factors[i] = divisor;
                    }
                    if (next_index == true && prime_factors[i] != divisor) {
                        // make sure next factor into array, except first factor case
                        i++;
                        prime_factors[i] = divisor;
                    }
                    num = num / divisor;
                }
                if (num % divisor != 0) {
                    // not a factor, then add up divisor until next factor rise
                    next_index = false;
                    divisor++;
                }
            }
            // in case number of factors beyond 3
            if (i == 0) {
                prime_factors[i] = divisor;
            }
            if (i > 0 && i < 2) {
                // in case last element in array is final num itself
                i++;
                prime_factors[i] = divisor;
                num = num / divisor;
            }
            // if num not equal to 1 means maybe more different factors afterwards
            if (i == 2 && num == 1) {
                is_num_Special = true;
            }
        }
        // Return true if and only if 'num' is special,otherwise false
        return is_num_Special;
    }
}
