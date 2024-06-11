package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        int occurenceDifferentPrimeNumbers = 0;
        int divideNumber = 2;
        int newNumberEachIteration = num;
        while (newNumberEachIteration != 1 && newNumberEachIteration > 0) {
            if (isPrime(divideNumber) && newNumberEachIteration % divideNumber == 0) {
                while (newNumberEachIteration % divideNumber == 0) {
                    newNumberEachIteration = (int) (newNumberEachIteration / (float) divideNumber);
                }
                occurenceDifferentPrimeNumbers++;
            }
            divideNumber++;
        }
        if (occurenceDifferentPrimeNumbers == 3) {
            return true;
        }
        return false;
        // Task 3: Return true if and only if 'num' is special
    }

    private static boolean isPrime(int number) {
        if (number <= 1)
            return false;
        for (int i = 2; i <= number - 1; i++) if (number % i == 0)
            return false;
        return true;
    }
}
