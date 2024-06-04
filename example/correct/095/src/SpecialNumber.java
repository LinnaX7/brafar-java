
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		for (int i=2; i<=num/2; i++){
			for (int j=3; j<=num/2; j++){
				for (int k=5; k<=num/2; k++){
					if (i*j*k == num){
						if (isPrime(i)){
							if (isPrime(j)){
								if (isPrime(k)){
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean isPrime(int num1){
		boolean prime = true;
		for (int i=2; i <= num1 / 2; i++){
			if (num1 % i == 0){
				prime = false;
				break;
			}
		}
		return prime;
	}
}
