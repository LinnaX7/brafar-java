
public class SpecialNumber {
	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int uniqueCount = 0;
		int limitCount = num;
		// Generate factors
		for (int factor=2; factor<limitCount/2; factor++) {
			// Check if factor is a prime number
			boolean isPrime = true;
			for (int i=2; i<factor; i++) {
				if (factor%i == 0) {
					isPrime = false;
					break;
				}
			}
			// Divide the number with the prime factor (if the factor is prime and the number is divisble)
			if (isPrime && (num%factor == 0)) {
				uniqueCount++;
				while (num%factor == 0 && num != 1) {
					num /= factor;
				}
			}
			//System.out.println("Factor in Loop: " + factor);
			//System.out.println("Is prime number?  " + isPrime);
			//System.out.println("Current Situation after dividing: " + num);
			if (num == 1) {
				//System.out.println("num == 1");
				break;
			}

		}
		if (uniqueCount == 3) {
			return true;
		}
		return false;
	}
}
