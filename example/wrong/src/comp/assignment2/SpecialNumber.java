package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int counter = 0;
        int index = 2;
        while (num != 1) {
            if (num%index==0) {
                counter++;
            }
            while (num % index == 0) {
                num = num / index;
            }

            index++;


        }
        if (counter == 3) {
            return true;
        } else {
            return false;
        }


    }
    public static void main (String[] args ){
        System.out.println(isSpecial(210));

    }
}

