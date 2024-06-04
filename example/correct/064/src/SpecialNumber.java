
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		int numt=0;
		if(num<=0)
			return false;
		for(int i=2;i<=num;i++) {
			if(num%i==0) {
				numt++;
				num/=i;
				while(num%i==0)
					num/=i;
			}
		}
		if (numt==3)
			return true;
		// Task 3: Return true if and only if 'num' is special
		return false;
	}


}
