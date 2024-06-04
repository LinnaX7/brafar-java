


public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		int primeCount = 0;
		boolean isSpecialPrimeDetected = false;
		boolean isFactorPrime = false;

		for (int i = 2; i <= num; i++){

			if (num % i == 0) {

				isFactorPrime = true;

				//For every factor of num, scan if it is a prime number by dividing values of j. If it is divisible, that factor i is not a prime number and will not be counted.
				for (int j = 2; j <= i/2; j++){

					 if ((i % j) == 0) {
					 	isFactorPrime = false;
						break;
					}
				}


				if (isFactorPrime == true) {
					primeCount++;
					//System.out.println(i);
				}

			}

		}

		//As per the requirement, if the the prime factor is EXACTLY 3, it is flagged as a special prime.
		if (primeCount == 3)
			isSpecialPrimeDetected = true;
		else if (primeCount != 3)
			isSpecialPrimeDetected = false;

		//System.out.println(primeCount);

		return isSpecialPrimeDetected;
	}


}
