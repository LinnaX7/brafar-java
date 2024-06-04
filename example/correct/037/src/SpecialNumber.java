
public class SpecialNumber {


	public static boolean isSpecial(int num) {

		int []prime_factors=new int[3]; // an array to store different factors
		int divisor= 2;  //initialize factor
		int i=0; //record index in array
		boolean is_num_Special=false; //judge whether num is special number
		boolean next_index; //check whether factor appear


		if(num>=2){
			while(num!=divisor && i<2){ //at most 3 elements in array means special num needs only 3 prime factors
				if(num%divisor==0){  //a prime factor appear - trigger condition
					next_index=true; //a factor appear
					if(next_index==true&&prime_factors[0]==0) {  //make sure first prime factor into array, next factor should evaluate through second if-condition
						prime_factors[i] = divisor;

					}
					if(next_index==true&&prime_factors[i] != divisor) { //make sure next factor into array, except first factor case
						i++;
						prime_factors[i] = divisor;
					}

					num = num / divisor;
				}

				if(num%divisor!=0){  //not a factor, then add up divisor until next factor rise
					next_index=false;
					divisor++;
				}
			}
			//in case number of factors beyond 3
			if(i==0) {
				prime_factors[i] = divisor;
			}
			if(i>0&&i<2){ //in case last element in array is final num itself
				i++;
				prime_factors[i] = divisor;
				num = num / divisor;
			}


			if(i==2&&num==1){is_num_Special=true;} //if num not equal to 1 means maybe more different factors afterwards

		}


		// Return true if and only if 'num' is special,otherwise false
		return is_num_Special;
	}

}
