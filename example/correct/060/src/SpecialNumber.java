
public class SpecialNumber {
	public static void main(String[] args) {
//        System.out.println(isSpecial(30));
//        System.out.println(isSpecial(210));
//        System.out.println(isSpecial(105));
	}

	public static boolean isSpecial(int num) {
		if (num<30){
			return false;
		}
		int i=2;
		int numberOfPrimeFactors=0;
		while (i<num){
			if (isPrime(i)){
				if (num%i==0){
					numberOfPrimeFactors++;
				}
			}
			i++;
		}
		return numberOfPrimeFactors == 3;
	}

	public static boolean isPrime(int num){
		for (int i=2; i < num; i++) {
			if (num%i==0){
				return false;
			}
		}
		return true;
	}
}
