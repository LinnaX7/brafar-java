
public class SpecialNumber {
	public static boolean isPrime(int x){
		if(x<=1){
			return false;
		}
		else {
			for (int i=2;i<x;i++){
				if(x%i==0){
					return false;
				}
			}
		}
		return true;
	}
	public static int[] returnlist(int num){
		int[] list=new int[num];
		int count=1;
		list[0]=num;
		if(!isPrime(num)){
			for(int x=2;x<num;x++){
				if(num%x==0){
					list[count]=x;
					count++;
				}
			}
		}
		return list;
	}
	public static boolean isSpecial(int num){
		// Task 3: Return true if and only if 'num' is special
		if(num<30){return false;}
		int Specialnumber=0;
		int[] list=new int[100];
		list=returnlist(num);
		for(int i:list){
			if(isPrime(i)){Specialnumber++;}
		}
		if (Specialnumber==3){return true;}
		return false;
	}
}