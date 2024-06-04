
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int cnt=0;
		for(int x=2;num>1;++x) {
			if(num % x == 0) {
				++cnt;
				while(num % x == 0) num/=x;
			}
		}
		return (cnt == 3);
	}


}
