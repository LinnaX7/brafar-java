package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int i = 2;
        int h = 0;
        int j = 0, primefactor = 0, prime = 0;
        i = 2;
        while (i <= num) {
            if (num % i == 0) {
                prime = 1;
                h = i / 2;
                for (j = 2; j <= h; j++) {
                    if (i % j == 0) {
                        prime = 0;
                        break;
                    }
                }
                if (prime == 1) {
                    primefactor += 1;
                }
            }
            i = i + 1;
        }
        if (primefactor == 3) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        if (isSpecial(210))
            System.out.println("TRUE");
        else
            System.out.println("FALSE");
    }
}
