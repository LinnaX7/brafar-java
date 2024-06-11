package comp.assignment2;

public class SpecialNumber {

    public static void main(String[] args) {
        for (int i = 2; i < 900; i++) {
            if (isSpecial(i)) {
                System.out.println(i);
            }
        }
    }

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int countDifferentPrime = 0;
        int currentDivideNumber = 2;
        int currentNumber = num;
        while (currentNumber != 1 && currentNumber > 0) {
            if (isPrime(currentDivideNumber) && currentNumber % currentDivideNumber == 0) {
                while (currentNumber % currentDivideNumber == 0) {
                    currentNumber /= (float) currentDivideNumber;
                }
                countDifferentPrime++;
            }
            currentDivideNumber++;
        }
        if (countDifferentPrime == 3) {
            return true;
        }
        return false;
    }

    private static boolean isPrime(int number) {
        if (number <= 1)
            return false;
        for (int i = 2; i <= number - 1; i++) if (number % i == 0)
            return false;
        return true;
    }
}
