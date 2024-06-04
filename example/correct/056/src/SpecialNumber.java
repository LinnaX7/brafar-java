//20050394d Lau Sin Man (Task 3)
import java.util.Scanner;

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		final int smaPrimeNum = 2; // initialize the smallest prime number
		int count = 0;
		int temp = 0; //for checking whether the prime factors are the same
		for (int i = smaPrimeNum; i <= num; i++){
			if(num%i == 0){
				count++;
				if (count == 1){
					temp = i;
				} else if (i == temp){ //if the prime factor is the same as the previous one, that prime factor will not be counted
					count--;
				}
				num = num/i;
			}
		}
		return count == 3; // if count is 3, return true, else return false
	}

	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		System.out.print("Please enter a positive number: ");
		int input = scan.nextInt();
		boolean specialNum = isSpecial(input);
		if (specialNum) { //if there are 3 different prime factors
			System.out.println(input + " is special.");
		} else {
			System.out.println(input + " is not special.");
		}
	}
}
