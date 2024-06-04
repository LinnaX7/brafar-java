
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int i=2;
		int counter=0;
		int temp=0;
		while(i*i<=num){
			if(num%i!=0){
				i++;
			}
			else{
				num=num/i;
				if (i!=temp){
					counter++;
					temp=i;
				}
			}
		}
		if(num>1){
			counter++;
		}
		if(counter==3)
			return true;
		else
			return false;
	}


}
