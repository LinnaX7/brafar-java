
import java.util.HashSet;
import java.util.Set;

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		Set<Integer> primeFactors = new HashSet<>();//to aviod counting the same numbers twice
		if(num==1) return false;//1 is not special
		for(int i=2; i<=num; i++){
			while(num%i==0 && i!=num){
				num=num/i;//get prime factors
				primeFactors.add(i);
			}
			if(num==i){
				primeFactors.add(i);
				break;
			}
		}
		if(primeFactors.size()==3){//decide whether it's special
			return true;
		}
		return false;

	}
}
