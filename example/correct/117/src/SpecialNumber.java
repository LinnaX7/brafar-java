
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		int primeCount = 0, i = 2;
		while ((i <= num) && (primeCount <= 3)) {
			if (num % i == 0) {
				if (checkPrime(i)) {
					primeCount++;
				}
			}
			i++;
		}
		return (primeCount == 3);
	}

	public static boolean checkPrime(int number) {
		for (int i = 2; i <= number / 2; i++) {
			if (number % i == 0) {
				return false;
			}
		}
		return true;
	}
}
