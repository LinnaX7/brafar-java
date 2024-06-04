
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		if (num<30) { // 30 is the smallest special number (2*3*5), and exclude negative numbers
			return false;
		}
		boolean[] list = primeList(num);
		int count = 0;
		for (int i=2;i<list.length;i++) {
			if (!list[i] && num % i == 0) {
				count++;
				while (num % i == 0) { // Clean out the same prime numbers
					num /= i;
				}
			}
		}
		return count==3;
	}

	private static boolean[] primeList(int num) {
		// to get a list of prime numbers
		boolean[] prime = new boolean[num];
		for (int i=2;i==num;i++) {
			for (int j=2;i*j<num;j++) {
				prime[i*j] = true;
			}
		}
		return prime;
	}
}
