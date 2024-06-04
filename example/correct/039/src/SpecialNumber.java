
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int cnt=1;
		for(int i=2;i<num;i++){
			if(num%i==0){
				cnt++;
				while(num%i==0){
					num=num/i;
				}
			}
		}
		if (cnt==3){
			return true;
		}
		return false;
	}

}
