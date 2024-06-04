
public class SpecialNumber {


	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int counter = 0;
		int number = 0;


		for (int i = 2; i <= num; ) {
			while (num % i == 0) {
				counter++;
				num /= i;
				if (counter != 0) {

					number++;
				}
			}
			i++;
			counter = 0;

		}
		if (number == 3) {
			return true;
		} else {
			return false;
		}

	}
	public static void main (String[] args){


		int x[] = {30,210,4};
		int y[] = {30,210,4};
		int count=0;
		int numbers = 0;


		for(int j=0;j<=2;j++) {


			if (isSpecial(x[j])) {

				for (int i = 2; i <= x[j]; ) {
					while (x[j] % i == 0) {
						count++;
						x[j] /= i;
					}
					if (count != 0) {

						numbers++;
						System.out.print(i);
						System.out.print("^");
						System.out.print(count);
						if (x[j] != 1) {
							System.out.print("*");
						}


					}
					i++;
					count = 0;
				}

			}
		}

	}

}
