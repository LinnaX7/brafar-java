package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int count = 0;
        int ans = 1;
        int temp = num;
        for (int i = 2; count < 3 && i < num; i++) {
            if (temp % i == 0) {
                count++;
                ans *= i;
                while (temp % i == 0) {
                    temp /= i;
                }
            }
        }
        return (count == 3) && (ans == num);
    }
}
