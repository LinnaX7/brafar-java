
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int testNumber = 2, factorCount = 0;
		while (num > 1) {
			boolean uniqueFactor = true;
			while (num % testNumber == 0) {
				num /= testNumber;
				if (uniqueFactor) {
					++factorCount;
				}
				uniqueFactor = false;
			}
			++testNumber;
			if (factorCount > 3) {
				return false;
			}
		}
		return factorCount == 3;
	}
}
