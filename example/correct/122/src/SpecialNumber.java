public class SpecialNumber {

	public static boolean isSpecial(int num) {
		int[] allfactors = new int[100000];
		int[] primeFactors = new int[100000];
		int primeFactorsize= 0;
		int count = 0;

		for (int i = 1; i <= num; i++) {
			if (num % i == 0) {
				allfactors[i - 1] = i;
			}

		}
		for(int i = 0; i < allfactors.length; i++){
			boolean isPrimenum = true;
			for(int j = 2; j< i ; j++){
				if(allfactors[i] % j == 0){
					isPrimenum = false;
					break;
				}
				if(allfactors[i] == 1 || allfactors[i] == 0){
					isPrimenum = false;
					break;
				}
				if(isPrimenum){
					primeFactors[primeFactorsize] = allfactors[j];
					primeFactorsize += 1;
					count= count + 1;
				}

			}
		}


		if(count == 3){
			return true;
		}
		else{
			return false;
		}




	}
}


