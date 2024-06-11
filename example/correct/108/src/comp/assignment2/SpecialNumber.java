package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int countv = 0;
        int nfactor = 0;
        for (int count = 2; count <= num; count++) {
            // finding all available number for division
            boolean flag = true;
            while (flag) {
                if (num % count == 0) {
                    // finding an available factor
                    num = num / count;
                    // System.out.println(num);
                    if (nfactor != count) {
                        // successful trial
                        countv++;
                    }
                    nfactor = count;
                } else {
                    // unsuccessful trial will make the flag false
                    flag = false;
                }
            }
        }
        if (countv == 3) {
            // return true if three prime factor
            return true;
        }
        return false;
    }
}
