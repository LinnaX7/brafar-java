
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int primes=0; //initiating number of primes to 0
		int prev_prime= 0;

		for(int i = 2; i< (num/2)+1; i++) {
		//Looping from 2 to num/2 because, mathematically factors cannot be greater than half of the number.
			while(num % i == 0) { //check if num%i==0
				num /= i;  //Divide num by i and store it in num
				if (i!= prev_prime){ //Important check to avoid duplication of primes
					prev_prime=i;
					primes++; //increse primes by 1
				}

			}
		}
		if(num > 2) { //cast when number is still more than 2, in that case, just print the number
			if (num!= prev_prime){
				prev_prime=num;
				primes++;
			}

		}

		return (primes==3); //check if primes == 3
	}


}
