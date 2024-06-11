package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        boolean flag = false;
        int numOfPrimeFactors = 0;
        for (int i = 2; i <= num / 2; i++) {
            // If i is a divisor of num
            if (num % i == 0) {
                // To judge whether i is prime numebr
                // 
                flag = isPrime(i);
            } else {
                flag = false;
            }
            // if i is prime number
            if (flag) {
                numOfPrimeFactors += 1;
            }
            // the the num of prime factors is more than 3, then return false
            if (numOfPrimeFactors > 3) {
                return false;
            }
        }
        // if the numberr of prime factors==3, then it's special
        if (numOfPrimeFactors == 3) {
            return true;
        }
        return false;
    }

    public static boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        } else if (num <= 3) {
            return true;
        }
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // 2*3*3*7 --> true
        int test1 = 126;
        System.out.println(isSpecial(test1));
        // 2*5*2*5 --> false
        int test2 = 100;
        System.out.println(isSpecial(test2));
        // 5*2*3 --> true
        int test3 = 3402;
        System.out.println(isSpecial(test3));
    }
}
