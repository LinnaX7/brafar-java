
public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special

		int i =0;
		int j =0;
		int k = 0;
		double m= 0;
		boolean b = false;
		int t=(int)Math.sqrt(num);

		for (i=0;i<t;i++){
			for (j=0;j<t;j++){
				for (k=0;k<t;k++){
					m =Math.pow(2,i)*Math.pow(3,j)*Math.pow(5,k);
					if (m==Math.pow(num,2)){b=true;break;}

				}
			}
		}


		return b;
	}


}
