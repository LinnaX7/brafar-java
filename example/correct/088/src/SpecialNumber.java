
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		int i = 2;
		int result, primeCounter, temp, check;
		int [] primeFactors = {0, 0, 0};
		int [] primePowers = new int[3];

		primeCounter = 0;
		temp = num;
		result = 1;

		while (i <= temp) {
			if ((temp % i == 0) && (primeCounter != 3)) {

				primePowers[primeCounter] = 1;
				primeFactors[primeCounter] = i;
				temp = temp / i;

				result = result * primeFactors[primeCounter];

				while (temp % i == 0) {

					primePowers[primeCounter]++;
					temp = temp / i;

					result = result * primeFactors[primeCounter];
				}
				primeCounter++;
			}

			if (primeCounter >= 3) {
				break;
			}

			i++;
		}

		check = (primeFactors[0] * primeFactors[1] * primeFactors[2]);

		if ((result == num) && (check != 0)) {
			return true;
		} else {
			return false;
		}
	}


}
