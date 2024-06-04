
public class SpecialNumber {
	static boolean isRoot(int aNum){ //create a method that checks whether the number is prime number or not
		float temp = (float) aNum;
		double aDouble = Math.round(temp);
		//create an array filled with prime numbers from 1-100
		int[] listOfPrimeNumbers={2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97};
		for(int k=0; k<listOfPrimeNumbers.length;k++){
			if(listOfPrimeNumbers[k]==aNum){
				return true;
			}
		}
		return false;
	}

	public static boolean isSpecial(int num) { //signature method
		int primers = 0, counter=0;

		for (int n = 1; n<=num; n++){
			int j = num % n;
			if(j==0){
				if(isRoot(n)==true){ //if isRoot() returns true, adds 1 to the counter
					counter++;
				}
			}
		}
		if(counter==3){ //if the counter has more or less than 3, it will return false
			return true;
		}
		else {
			return false;
		}
	}
}
