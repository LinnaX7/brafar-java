
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		//init counter to store the number of prime factors, init previousFactor to store the last factor found.
		int counter=0, previousFactor=-1;

		//while loop for iterating all prime factors and counting them but avoid repeat counting
		while (num != miniFactor(num)){
			if (previousFactor != miniFactor(num)) {
				previousFactor = miniFactor(num);
				counter++;
			}
			num /= miniFactor(num);
		}

		//ensure the last prime factor won't be counted repeatedly
		if (previousFactor != miniFactor(num)) counter++;

		//counter exactly should be 3 if True is returned
		return counter == 3;
	}

	private static int miniFactor(int num){
		/*
		Built for finding the smallest factor of a num except 1
		Feature: always get the smallest factor which is a prime/ when num is a prime, num itself will be returned
		Input: a number
		Output: the smallest factor of the number except 1
		* */
		int i;

		for (i = 2; i<num; i++) { if (num%i == 0)  return i;}

		return num;
	}
}
