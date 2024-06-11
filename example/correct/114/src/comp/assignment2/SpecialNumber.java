package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int counting = 0;
        int divided = 2;
        int half = num / 2;
        while (num != 1 && divided <= half) {
            if (num % divided == 0) {
                counting++;
                while (num % divided == 0) {
                    num = num / divided;
                }
            }
            divided++;
        }
        if (counting == 3) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        SpecialNumber unique = new SpecialNumber();
        if (unique.isSpecial(30))
            System.out.println("30 is special!");
        else
            System.out.println("30 is not special!");
        if (unique.isSpecial(210))
            System.out.println("210 is special!");
        else
            System.out.println("210 is not special!");
        if (unique.isSpecial(4))
            System.out.println("4 is special!");
        else
            System.out.println("4 is not special!");
    }
}
