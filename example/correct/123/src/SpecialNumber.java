
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		int temp = 0;
		int counter = 0;

		for (int i = 2; i<=num ;i++) {
			if (num % i == 0) {
				num /= i;

				if (temp != i) {
					counter++;
				}

				if (num == 1 && counter == 3) {
					return true;
				}

				i = 1;
			}
		}
		return false;
	}


}
