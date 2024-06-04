
import java.util.ArrayList;
import java.util.List;

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		// this program is to test POSITIVE numbers
		if(num <0)
			return false;

		ArrayList<Integer> primeFactors = new ArrayList<Integer>();

		for(int i = 2 ; i < num ; i++){
			if(isPrime(i) && (num % i == 0 )){
				primeFactors.add(i);
				// System.out.println(i);
			}
		}

		// if less than 3 prime factors retur nfalse
		if(primeFactors.size() < 3)
			return false;

		List<int[]> combinations = generateCombinations(primeFactors.size());

		for (int[] combination : combinations){
			//System.out.println(Arrays.toString(combination));
			int prime1 = primeFactors.get(combination[0]) ,  prime2 = primeFactors.get(combination[1]) ,  prime3 = primeFactors.get(combination[2]);
			int testNum = num;

			while(testNum % prime1 ==0)
				testNum = testNum / prime1;


			while(testNum % prime2 ==0)
				testNum = testNum / prime2;


			while(testNum % prime3 ==0)
				testNum = testNum / prime3;


			if(testNum ==1)
				return true;

		}
		return false;


	}

	static boolean isPrime(int num){
		if(num <=1){
			return false;
		}
		for (int i = 2 ; i < num ; i ++){
			// System.out.println(i);
			if(num%i == 0){
				return false;
			}
		}
		return true;
	}


	static List<int[]> generateCombinations(int n) {
		List<int[]> combinations = new ArrayList<>();
		for (int i = 0; i < n - 2; i++) {
			for (int j = 1; j < n - 1; j++) {
				for (int k = 2; k < n; k++) {
					if(k <= j || j <= i || k <= i)
						continue;
					else {
						int[] combination = {i,j,k} ;
						combinations.add(combination);
					}
				}

			}
		}
		return combinations;
	}


}
