
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int cnt = 0;
		for(int i=2; i<=num; ++i){
			if(num % i == 0) {
				cnt++;
				while (num % i == 0)
					num /= i;
			}
			if(num == 1)
				break;
		}
		return cnt == 3;
	}


}
