
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		List<Integer> primeFactor = new ArrayList<>();

		for (int i = 2; i <= num; i++){ //start from 2, try to find the prime factors
			while(num%i == 0){ //if there are no remainders, add that integer into the arrayList
				primeFactor.add(i);
				num = num/i;
			}
		}

		Set<Integer> hashset = new LinkedHashSet(primeFactor); // avoiding duplicates
		List<Integer> primeFactorNoDup = new ArrayList<>(hashset);

		if (primeFactorNoDup.size() == 3){
			return true; // return true if there are exactly three different prime factors
		} else {
			return false;
		}
	}
}
