public class SpecialNumber {
	static int sum = 0;

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int i = 2;
		int result = 1;
		boolean final_result = false;
		while (i * i <= num) {
			while (num % i == 0) {
				result = result + 1;
				num = num / i;
			}
			i++;
		}
		if (num > 1) {
			if (result == 3) {
				final_result =  true;
			} else {
				final_result = false;
			}
		}

		return final_result;
	}
}