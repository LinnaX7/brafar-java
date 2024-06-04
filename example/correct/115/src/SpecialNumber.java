

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		if (num>0){
		int count =0;
		int lastone = 0;

		for(int i=2; i<num+1; i++){
			if(num%i == 0){
				num = num/i;
				count++;
				if (i==lastone){
					count--;
				}
				lastone=i;
				i=1;
			}
		}
		if (count==3){
			return true;
		}
		else return false;
	}
		return false;
	}


}
