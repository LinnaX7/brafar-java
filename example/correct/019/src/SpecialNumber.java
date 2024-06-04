import java.util.*;
public class SpecialNumber {

	//Validate which number is prime
	public static boolean checkPrime(int num) {
		//!prime if can be divided any numbers beside 1 and itself
		for (int i = 2; i < num; i++) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		//Use it to store special number
		ArrayList<Integer> numArr = new ArrayList<Integer>();
		//loop through every number starting from 2
		for (int i = 2; i <= num; i++) {
			if (checkPrime(i) && num % i == 0) {
				//Store different special number
				//store the special number once (not storing duplicate)
				numArr.add(i);
				//After validating the prime number we divide the numbers until it cant be divided
				while (num % i == 0) {
					num /= i;
				}
				//if number is 1 means it can't be further divided
				if (num == 1) {
					break;
				}
			}
		}
		if(numArr.size()==3){
			//System.out.print(numArr);           //for testing and debug
			//System.out.println(numArr.size());  //for testing and debug
			return true;
		}else{
			//System.out.print(numArr);           //for testing and debug
			//System.out.println(numArr.size());  //for testing and debug
			return false;
		}
	}
	/*
	//for testing
	public static void main(String [] args){
		//System.out.print(isSpecial(120));
		//System.out.print(isSpecial(8));
	}
    */
}

