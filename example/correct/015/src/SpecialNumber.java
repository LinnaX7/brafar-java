import java.lang.Math;

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int primeFactorCount = 0;
		for(int i=2; i<num; i++){
			if(isPrime(i) && num%i==0)
				primeFactorCount++;
		}
		if(primeFactorCount==3)
			return true;
		return false;
	}

	static boolean isPrime(int num) {
		if(num <=1) return false;
		for(int i=2; i<=Math.sqrt(num); i++) {
			if(num%i==0) return false;
		}
		return true;

	}


}
