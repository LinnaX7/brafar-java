package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // 1 is not a prime number!!!!!!
        int[] ar = new int[3];
        int a = 0;
        for (int i = 2; i <= num; i++) {
            while (num % i == 0) {
                if (a == 3) {
                    return false;
                }
                num = num / i;
                ar[a] = i;
                a++;
            }
        }
        if (ar[0] != ar[1] && ar[0] != ar[2] && ar[1] != ar[2]) {
            return true;
        }
        return false;
    }
}
