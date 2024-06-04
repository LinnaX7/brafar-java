
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int[] allprime=getallprime(num);
		int count=0,i,result=num;
		for(i=0;i<allprime.length;i++) {
			if(result==1&&count<3)
				return false;
			while(result%allprime[i]==0) {
				result = result / allprime[i];
				count++;
			}
		}
		if(result==1&&count==3)
			return true;
		return false;
	}
	public static int[] getallprime(int num){
		int[] allprime={};
		int i,j;
		boolean flag;
		for(i=2;i<num;i++) {
			flag = true;
			j = 2;
			while (j < i && flag) {
				if (i % j == 0)
					flag = false;
				j++;
			}
			if(flag) {
				allprime = addelementto(allprime,i);
			}
		}
		return allprime;
	}
	public static int[] addelementto(int[] num,int element){
		int[] newnum = new int[num.length+1];
		int i;
		for(i=0;i<num.length;i++)
			newnum[i]=num[i];
		newnum[i]=element;
		return newnum;
	}
}
