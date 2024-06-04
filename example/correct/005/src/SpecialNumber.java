

public class SpecialNumber {

public static void main(String[] args){
	new SpecialNumber();
}
	public static boolean isSpecial(int num) {
		int count = 0;
		int primetester = 2;
		int midpt = num/2;

		while (num!= 1 && primetester <= midpt) {
			if (num%primetester == 0) {
				count++;
				while (num%primetester == 0) {
					num = num/primetester;
				}
			}

			primetester++;

		}


		if (count == 3)
			return true;

		return false;

	}
}



