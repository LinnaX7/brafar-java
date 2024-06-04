
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		/* Find out all the prime factor of this number. */
		String primeFactor="";
		int j=0;
		for (int i=2;i<=num;i++){
			while (j==0&&num!=1){
				if (num%i==0){
					num=num/i;
					if(num==1) {primeFactor=primeFactor.concat(Integer.toString(i));}
					else {primeFactor=primeFactor.concat(i+"*");}
				}
				else {j=1;}
			}
			j=0;
		}
		String[] strPrimeFactor= primeFactor.split("\\*");

		/* Calculate the number of different prime factors. */
		int PrimeFactorNum=strPrimeFactor.length;
		int TruePrimrFactorNum=1;
		for(int m=1;m<PrimeFactorNum;m++){
			if (!strPrimeFactor[m].equals(strPrimeFactor[m-1])){
				TruePrimrFactorNum++;
			}
		}

		if (TruePrimrFactorNum==3) {return true;}

		return false;
	}


}
