
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		// We start from the smallest prime number, which is 2
		if(num==0) return false;
		int count =0;
		while (num%2==0) {
			//count will only be 1 when the prime factors are still 2, which are equal
			count=1;
			num=num/2;
		}
		//Loop After 2
		for(int i =3; i<=num;i+=2){
			boolean isPrimeFactor = false;
			while(num%i==0){
				isPrimeFactor=true;
				num=num/i;
			}
			if(isPrimeFactor){
				count++;
			}
		}
		if(count==3) return true;
		return false;
	}


}
