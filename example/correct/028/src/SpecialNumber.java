
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		int i=0;
		int counter=0;
		int temp=num;
		for (i=2;i<temp;i++){
			if (num%i==0){
				if (primefactor(i)){
					counter++;
					num=num/i;
				}
			}
		}
		if(counter==3)
			return true;
		else
			return false;
	}
	public static boolean primefactor(int g){
		int test=0;
		if (g==2)
			return true;
		for (test=2;test<g;test++){
			if (g%test==0)
				return false;
		}
		return true;}
}

