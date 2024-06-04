
import java.util.Scanner;

public class SpecialNumber {

	public static void main(String[] args){
			Scanner scan= new Scanner(System.in);
			int num= scan.nextInt();
			isSpecial(num);
		}
		public static boolean isSpecial(int num) {
			int count = 0;
			int divisor = 2;
			int mid = num/2;

			while(num!=1 && divisor <= mid ){

				if(num%divisor==0){
					count++;
					while(num%divisor==0){
						num = num/ divisor;
					}
				}
				divisor++;
			}

			if(count == 3)
				return true;
		return false;
	}


}
