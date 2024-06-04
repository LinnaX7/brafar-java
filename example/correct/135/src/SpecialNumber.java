
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int count = 0;
		if(num > 0){
			for(int i = 2; i * 2 <= num; i++){
				if(num % i == 0) {
					boolean flag = true;
					for(int j = 2; j * j <= i; j++){
						if(i % j == 0){flag = false;}
					}
					if(flag){count++;}
				}
			}
			if(count == 3){return true;}
			else{return false;}
		}
		return false;
	}
}
