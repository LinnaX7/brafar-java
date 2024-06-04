
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		int use;
		int count=0;


		for(int x = 2; x < 10; x++){
			use=num%x;

			if(use==0){
				count= count+1;
			}
			if(use==0){
				num=num/x;
			}

		}
		if(count==3 && num==1){

			var b = true;
			return b;

		}
		return false;
	}


}
