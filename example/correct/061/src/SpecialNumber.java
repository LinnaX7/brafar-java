
import java.util.ArrayList;

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		ArrayList<Integer> list = new ArrayList<Integer>(0);
		for	(int i=2;i<=num;i++){
			if(num%i==0){
				boolean isPrime = true;
				for (int j=2; j<=i/2; j++){
					if (i%j==0){
						isPrime=false;
						break;
					}
				}
				if(isPrime){
					list.add(i);
				}
			}
		}
		if (list.size()==3){
			return true;
		}
		else {
			return false;
		}
	}


}
