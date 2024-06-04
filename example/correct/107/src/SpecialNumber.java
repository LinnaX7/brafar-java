
public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int counter1 = 0;
        int jump = 2;
        for (int i = 2; i < num; i = jump){
            if (num % i == 0){counter1 += 1;}
            if (counter1 > 3){return false;}
            jump = SpecialNumber.nextPrimeNum(jump);
        }
        if (counter1 < 3){return false;}
        return true;
    }

    static int nextPrimeNum(int num){
        int result = num;
        int counter1;
        while(true){
            counter1 = 0;
            result += 1;
            for (int i = 2; i < result + 1; i++){
                if (result % i == 0){counter1 += 1;}
                if (counter1 > 1){break;};
            }
            if (counter1 == 1){break;};
        }
        return result;
    }

}
