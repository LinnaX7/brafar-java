
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		int[] array = new int[10];
		int count = 0;

		for(int i = 2; i < num; i++) {
			while(num % i == 0) {
				array[count]++;
				num = num / i;
				if (num != i){
					count++;
				}
			}
		}
		if(num > 2) {
			array[count]++;
		}

		int sum = 0;
		int one = 0;
		for(int value = 0; value < array.length; value++){
			if (array[value] == 1) {
				one++;
				sum = sum + array[value];
			}
			else if (array[value] > 1)
				break;
		}
		if (one == 3 && sum == 3 )
			return true;
		else
			return false;
	}
}
