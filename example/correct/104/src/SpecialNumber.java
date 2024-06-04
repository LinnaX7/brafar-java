
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int x = 0; //count the number of the prime factors of num//
		int y = 2; //the number by which num is going to be divided in the coming loop, namely the divisor//
		int half = num/2;

		//loop to see how many prime factors of num are there//
		while (num != 1 && y <= half) { //every divisor of a number, except for that number itself, is at most half of the number//
			if (num % y == 0) { //if num is divisible by y, that means y is one of its factors//
				x++; //increased by 1 whenever a prime factor occurs//
				while (num % y == 0) {
					num = num / y; //execute division//
				}
			}
			y++; //the divisor is increased by 1 to see if it can divide num again in the while loop//
		}

		if (x == 3) { //if there are exactly three different prime factors of num, it is special//
			return true;
		}
		else {
			return false;
		}
	}
}
