
public class SpecialNumber {
	public static boolean isPrime(int n){
    	if(n<2){
    		return false;
		}else if(n==2){
    		return true;
		}else{
    		int squareRoot=(int)Math.sqrt(n);
    		for(int i=2;i<=squareRoot;i++){
    			if(n%i==0){
    				return false;
				}
			}
    		return true;
		}
	}

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int count = 0;
		for (int i = 2; i <= num; i++) {
			if (num % i == 0) {
				if (isPrime(i)) {
					count++;
				}
			}
		}
		if(count == 3){
			return true;
		}
		return false;
	}

}
