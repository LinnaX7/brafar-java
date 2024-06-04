
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		if( num <= 0 ) return false;
		int i = 1, Num = num, cnt = 0;
		while( cnt < 3 ) {
			if( Num == 1 ) return false;
			i ++;
			if( Num % i == 0 ) cnt ++;
			while( Num % i == 0 ) Num /= i;
		}
		if( Num == 1 && cnt == 3 ) return true;
		return false;
	}
}
