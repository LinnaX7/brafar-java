
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int i = 2, cnt = 0;
		boolean first = false;
		while(num >= i) {
			while(num % i == 0){
				if (first == false)	{
					first = true;
					cnt++;
				}
				num /= i;
			}
			i++;
			first = false;
		}

		if (cnt == 3) return true;
		else return false;
	}


}
