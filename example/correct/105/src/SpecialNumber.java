
import java.util.HashSet;
import java.util.Set;

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		Set<Integer> prime = new HashSet<>(); // use a Set to store number, which is not duplicated in this set
		for(int i = 2;i <= num;i++){
			while(num%i == 0 && num>i) {
				num = num / i;
				prime.add(i);  // add the prime factor into Set
			}
			if(num == i){
				prime.add(i);
				break;
			}
		}

		if(prime.size() ==3)
			return true;
		else
			return false;
	}
}
