
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		//set an array to record the factor
		int[] factor = new int[4];
		int index=0;
		for (int i = 2; i < num; i++) {
			if(num%i==0){
				factor[index]=i;
				num=num/i;
				index=index+1;
				i--;
			}
			factor[index]=num;
		}
		int len=0,repeat=0;
		//calculate the factor number
		for (int x=0;x<4;x++){
			if(factor[x] != 0){
				len=len+1;
			}
		}
		//calculate the repeat times
		for (int u=0;u<4;u++){
			for(int y=u+1;y<4;y++){
				if (factor[u]==factor[y] & factor[u] !=0){
					repeat=repeat+1;
				}
			}
		}
		//calculate the real number of factors
		len=len-repeat;
		return len == 3;
	}
}
