
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		int count = 0;					//count for prime number
		for (int i=2; i<=num;i++) {
			if (num % i == 0) count++;
			while (num % i ==0) {
				num = num/ i;
			}

		}

		//checking for special number
		if (count == 3) return true;
		else return false;

	}

}
