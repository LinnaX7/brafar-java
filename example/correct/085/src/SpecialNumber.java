
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int counter=0; // initialize counter
		for (int i = 2; i<=num; i++){
			if (num % i ==0){  // if num can be divided by i
				counter++; // increment to counter
				while (num % i ==0){ // while i can still be divided by current divider
					num = num / i; // divide num by i
				}
			}

		}
		if (counter==3){ // if special counter meets 3
			return true; // return true
		}
		return false; // special counter can not meet 3
	}


}
