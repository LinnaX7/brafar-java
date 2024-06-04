
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int count = 0;
		int d = 2; //d=divisor
		int m = num/2; //m=middle

		while(num!=1 && d <= m ){

			if(num%d==0){
				count++;
				while(num%d==0){
					num = num/ d;
				}
			}

			d++;
		}

		if(count == 3)
			return true;
		return false;
	}

	public static void main(String[] args){
		SpecialNumber s = new SpecialNumber();

		if(SpecialNumber.isSpecial(30))
			System.out.println("'30' is special");
		else
			System.out.println("'30' is not special");


		if(SpecialNumber.isSpecial(210))
			System.out.println("'210' is special");
		else
			System.out.println("'210' is not special");

	}

}



