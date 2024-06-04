
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int [] numbers = new int [100];
		boolean [] prime = new boolean [100];

		prime[0] = true;
		for (int i = 2; i <= 100; i++){
			for (int j = i * 2; j <= 100; j = j + i){
				prime[j-1] = true;
			}
		}

		for (int i = 2; i <= num; i++){
			while(num%i == 0){
				num = num / i;
				numbers[i-1] = numbers[i-1] + 1;
			}
		}
		if (num > 2)
			numbers[num] = numbers[num];

		boolean flag = true;
		int count = 0;
		for (int i = 0; i < 100; i++){
			if (numbers[i] != 0){
				count = count + 1;
			}
		}
		if (count != 3)
			flag = false;

		for (int i = 0; i < 100; i++){
			if (prime[i] == true && numbers[i] > 0)
				flag = false;
		}

		return flag;
	}


}

