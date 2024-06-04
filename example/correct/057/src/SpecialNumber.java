
public class SpecialNumber{
    public static boolean isSpecial(int num) {
        if(num<=0){return false;}
        // Task 3: Return true if and only if 'num' is special
        int[] primeNumList = primeNumArr(num);  //create an array contain possible prime factors
        int listLength = primeNumList.length;
        int[] counterList = new int[listLength];    //for counting the number of factors of num
        boolean factorization = true;   //factorize num
        while (factorization) {
            for (int i = 0; i < primeNumList.length; i++) {

                if (num % primeNumList[i] == 0) {
                    num = num / primeNumList[i];
                    counterList[i]++;
                    i = 0;
                }
                if (num == 1) {
                    factorization = false;
                    break;
                }
            }
        }

        int primeCounter = 0;   //determine whether num has exactly 3 different prime
        for (int item : counterList) {
            if (item > 0) {
                primeCounter += 1;
            }
        }
        return primeCounter == 3;
    }

    //return an array which contains all possible prime factors for num
    static int[] primeNumArr(int num) {
        int primeCounter = 0;

        for (int a = 2; a <= num / 2; a++) {
            if (primeTest(a)) {
                primeCounter++;
            }
        }
        int[] primeNumArr = new int[primeCounter];
        int arrayCounter = 0;
        for (int a = 2; a <= num / 2; a++) {
            if (primeTest(a)) {
                primeNumArr[arrayCounter++] = a;
            }
        }

        return primeNumArr;
    }

    //return whether a number is prime
    static boolean primeTest(int num) {
        boolean notPrime = false;
        for (int i = 2; i <= num / 2; i++) {
            if (num % i == 0) {
                notPrime = true;
                break;
            }
        }
        return !notPrime;
    }

}

