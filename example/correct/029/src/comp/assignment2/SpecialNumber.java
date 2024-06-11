package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        if (num <= 2) {
            return false;
        }
        int[] prime_arr = new int[num];
        int size = 0;
        int count = 0;
        prime_arr[size] = 2;
        size++;
        boolean flag = false;
        for (int i = 3; i < num; i++) {
            flag = false;
            for (int j = 2; j < i; j++) {
                if (i % j == 0) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                prime_arr[size] = i;
                size++;
            }
        }
        if (size < 3) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (num % prime_arr[i] == 0) {
                count++;
            }
        }
        if (count == 3) {
            return true;
        }
        return false;
    }
}
