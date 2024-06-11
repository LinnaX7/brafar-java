package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        if (num <= 0) {
            return false;
        }
        // Task 3: Return true if and only if 'num' is special
        // create an array contain possible prime factors
        int[] primeNumList = primeNumArr(num);
        int listLength = primeNumList.length;
        // for counting the number of factors of num
        int[] counterList = new int[listLength];
        // factorize num
        boolean factorization = true;
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
        // determine whether num has exactly 3 different prime
        int primeCounter = 0;
        for (int item : counterList) {
            if (item > 0) {
                primeCounter += 1;
            }
        }
        return primeCounter == 3;
    }

    // return an array which contains all possible prime factors for num
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

    // return whether a number is prime
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
