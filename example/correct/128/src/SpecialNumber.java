
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int prime_factors = 0;
		for(int i = 2; i <= num/2; i++){
			if(num % i == 0){
				if(isPrime(i)){
					prime_factors++;
				}
			}
		}
		if(prime_factors == 3){
			return true;
		}

		return false;
	}

	public static boolean isPrime(int num) {
		int factor = 0;
		for(int i = 1; i <= num; i++){
			if(num % i == 0){
				factor++;
			}
		}
		if(factor == 2){
			return true;
		}

		return false;
	}


}
