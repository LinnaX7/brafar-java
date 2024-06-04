
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int number=num;
		int p=0; //different prime factors
		for (int i=2;i<=num;i++){
			if(number%i==0){
				p=p+1;
			}
			while(number%i==0){
				number=number/i;
			}
		}
		if(p==3){
			return true;
		}
		else
			return false;
	}


}
