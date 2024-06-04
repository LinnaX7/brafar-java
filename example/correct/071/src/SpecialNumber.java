
public class SpecialNumber {
	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		//initialization and declaration
		int factornum = 2; //show the what is the factor of number (num % remainder== 0 and factor has to be grater than 1)
		int primefactor=0; //how many prime number is divisible

		while (num >= 2) { //as a definition of prime (greater than 1)
			if (num % factornum == 0){
				primefactor = primefactor + 1; //increase number of prime factor

				while (num % factornum == 0) { // to reduce the redundant factor
					num = (num / factornum);
				}

				factornum = factornum + 1; //to find another prime divisible num
			}
			else {
				factornum = factornum + 1; //to find another prime divisible num
			}
		}
		if (primefactor == 3) {//only if it is divide with three different prime num
			return true;
		}
		else {
			return false;
		}
	}

}