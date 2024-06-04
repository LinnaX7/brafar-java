
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int count = 1; //at least 1 prime factor for num
		if (num <= 0) return false;
		int[] arr = new int[num];
		int quotient = num;
		int arrPointer = 0;
		/*Prime factors are extracted from lower value to higher value through remaining quotient for each division.*/
		for (int i = 2; i <= quotient; i++) {
			if (quotient % i == 0) { //get factors
				quotient = quotient / i;
				arr[arrPointer++] = i;
				i = 1; //becomes 2 through i++
			}
		}
		if (arr[0] == 0) return false;
		int curr = arr[0];
		for (int i = 1; i < num; i++) {
			if (arr[i] == 0) break;
			if ((arr[i] != curr) && (arr[i] != 0)) count++;
			if (count > 3) return false;
		}

		if (count != 3) return false;

		return true;
	}
}
