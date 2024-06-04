
public class SpecialNumber {

	public static void main(String[] args){
        System.out.println(isSpecial(60));
    }

	public static boolean isSpecial(int num) {
		int count = 0;
		for(int i =2;i<num;i++){
			if(isPrime(i)&num%i==0){
				count++;
			}
		}
		return count == 3;
	}

	public static boolean isPrime(int n){
		if(n==0||n==1){
			return false;
		}
		if(n==2){
			return true;
		}
		for(int i =2;i<n;i++){
			if(n%i==0){
				return false;
			}
		}
		return true;
	}
}
