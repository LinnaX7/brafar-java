package comp.assignment2;

public class SpecialNumber {

    public static void main(String[] args) {
        if (isSpecial(30)) {
            System.out.println("yes");
        } else {
            System.out.println("not");
        }
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special\
        if (num < 2) {
            return false;
        }
        int count = 0;
        int nowPrime = 2;
        while (num != 1) {
            if (count >= 3) {
                return false;
            }
            if ((num % nowPrime) == 0) {
                count++;
            }
            while ((num % nowPrime) == 0) {
                num = num / nowPrime;
            }
            nowPrime = Nextprime(nowPrime);
        }
        if (count == 3) {
            return true;
        }
        return false;
    }

    // judge n is prime or not
    public static boolean ispirme(int n) {
        if (n == 2) {
            return true;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static int Nextprime(int now) {
        // get the next prime number
        for (int i = now + 1; ; i++) {
            if (ispirme(i)) {
                return i;
            }
        }
    }
}
