package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int testPrime = 4;
        int accuPrime = 1;
        int noOfPrime = 0;
        if (num % (accuPrime * 2) == 0) {
            accuPrime = accuPrime * 2;
            noOfPrime++;
        }
        if (num % (accuPrime * 3) == 0) {
            accuPrime = accuPrime * 3;
            noOfPrime++;
        }
        while (testPrime <= num) {
            int control;
            int accuForTestPrime = 0;
            for (control = testPrime; control >= 2; control--) {
                if (testPrime % control == 0) {
                    accuForTestPrime++;
                }
            }
            if (accuForTestPrime == 1) {
                if (num % (accuPrime * testPrime) == 0) {
                    noOfPrime++;
                    accuPrime = accuPrime * testPrime;
                }
            }
            testPrime++;
        }
        if (noOfPrime == 3) {
            return true;
        }
        return false;
    }
}
