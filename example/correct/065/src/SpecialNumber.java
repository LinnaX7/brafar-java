
public class SpecialNumber {
    public static boolean isSpecial(int n)
    {
        if (n <= 2)
            return false;
        int k = 2, cnt = 0;
        while(n > 1) {
            if (n % k == 0) {
                cnt++;
                while (n % k == 0)
                    n /= k;
            }
            k ++;
        }
        return cnt == 3;
    }
}
