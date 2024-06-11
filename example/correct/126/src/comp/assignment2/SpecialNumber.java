package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int i = 0;
        int fac = 2;
        int mid = num / 2;
        while (num != 1 && fac <= mid) {
            if (num % fac == 0) {
                i++;
                while (num % fac == 0) {
                    num = num / fac;
                }
            }
            fac++;
        }
        if (i == 3) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        SpecialNumber s1 = new SpecialNumber();
        if (s1.isSpecial(130))
            System.out.println("The number is special!");
        else
            System.out.println("The number is not special!");
    }
}
