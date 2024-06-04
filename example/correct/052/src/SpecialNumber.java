
public class SpecialNumber {
	public static boolean isPrime (int num) {
		for (int i=2; i< num; i++) {
			if(num%i==0)
				return false;}
		return true;
	}

	public static boolean isSpecial(int num) {
			int flag=0;
			for (int i=2; i<num; i++) {
				if(num%i==0){
					if(isPrime(i)==true)
						flag+=1;
				}
				if (flag>3)
					return false;
			}
			if(flag==3)
				return true;
			else
				return false;		
	}


}
