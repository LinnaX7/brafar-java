package comp.assignment2;

public class SpecialNumber {

    static int prime(int findedprime, int inputnum) {
        int tprime = inputnum;
        if (findedprime == 1)
            return 2;
        for (int k = findedprime + 1; k <= inputnum; k++) {
            boolean isprime = true;
            for (int i = 2; i <= k / 2 + 1; i++) {
                if (k % i == 0) {
                    isprime = false;
                    break;
                }
            }
            if (isprime) {
                tprime = k;
                break;
            }
        }
        return tprime;
    }

    public static boolean isSpecial(int nums) {
        int[][] arr = new int[3][2];
        int finnall = 0, beginner = prime(1, nums), counterpart = nums;
        while ((finnall < 3 || counterpart != 1) && beginner <= nums / 2 + 1) {
            if (beginner == nums) {
                return false;
            }
            int expoent = 0;
            boolean literator = false;
            while (counterpart % beginner == 0) {
                expoent++;
                counterpart /= beginner;
                literator = true;
            }
            if (literator) {
                if (finnall <= 2) {
                    arr[finnall][0] = beginner;
                    arr[finnall][1] = expoent;
                }
                finnall++;
            }
            beginner = prime(beginner, nums);
        }
        if (beginner > nums / 2 + 1 || finnall != 3) {
            return false;
        }
        return true;
    }
}
