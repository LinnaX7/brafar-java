
public class SpecialNumber {
	public static boolean isSpecial(int num) {
		int i = 0, x = 2, mid = num / 2;

		while (x <= mid && num != 1) {
			if (num % x == 0) {
				num = num / x;
				i++;
				}
			x++;
		}

		if (i != 3)
			return false;
		else
			return true;
	}
}
