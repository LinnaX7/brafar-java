
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		int[] primeArray = new int[]{2,3,5,7,11,13,17,19,23,29};  //let an array to store 10 prime numbers
		int[] primeCount = new int[10];                           //let an array to store count of prime factors of num

		while (num>1){
			for (int j=0; j<10; j++){              //loop through prime numbers
				if (num % primeArray[j] == 0){
					primeCount[j]++;               //count of that prime factor + 1
					num /= primeArray[j];
				}
			}
		}

		int count = 0;
		for (int j=0; j<10; j++){
			if (primeCount[j] != 0) {
				count++;
			}
		}
		if (count==3){           //if num has exactly 3 different prime factors
			return true;         //it is special.
		}
		return false;            //otherwise, it is not special.
	}


}
