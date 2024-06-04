
import java.util.Scanner;

public class SpecialNumber {
	public static void main(String[] args){
		Scanner input=new Scanner(System.in);
		int num=input.nextInt();
		System.out.println(isSpecial(num));
	}

	public static boolean isSpecial(int num) {

		// Task 3: Return true if and only if 'num' is special
		int count = 0;
		int or = num;
		boolean result = false;
		for (int i = 2; i < or; i++){
			if (isPrime(i) == true){
				if (num%i == 0){
					num = num/i;
					count = count + 1;
				}
			}
		}
		if (count == 3){
			result = true;
		}
		return result;
	}

	public static boolean isPrime(int number){
		for (int i = 2;i<number;i++){
			if (number % i == 0){
				return false;
			}
		}
		return true;
	}


}
