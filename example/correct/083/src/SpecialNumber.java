
import java.util.ArrayList;

public class SpecialNumber {

	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		if (num < 0){
			return false;
		}

		//Test t = new Test();
		ArrayList<Integer> PrimeNum = primeNumbers();
		//System.out.println(PrimeNum);

		int compare = 1;
		//System.out.println("0 index : " + compare);
		int size = PrimeNum.size();

		for (int i = 0; i < 3; i++){
			compare *= PrimeNum.get(i);
			//System.out.println("multiple prime number :" + PrimeNum.get(i));
			//System.out.println("compare number :" + compare);
			if (num == compare){
				return true;
			}
		}

		//System.out.println("======= <Stage 2> =========");

		for (int i = 0; i < size-2; i++){
			int elementA = PrimeNum.get(i);

			for (int j = 1; j < size; j++){
				int elementB = PrimeNum.get(j);

				for (int k = 2; k < size; k++){
					int elementC = PrimeNum.get(k);
					if (elementA != elementB && elementB != elementC){

						for (int x = 1; x  < 6; x++){
							for (int y = 1; y < 6; y++){
								for (int z = 1; z < 6; z++){
									compare = power(elementA, x) * power(elementB, y) *
											power(elementC, z);
									if(num == compare){
										System.out.println("element A, B & C :" + elementA +"^"
												+ x +" "+ elementB + "^" + y
												+ " "+ elementC + "^" + z
												+ " = " + compare);
										return true;
									}
								}
							}
						}
					}
				}
			}

		}

		return false;
	}

	public static int power(int base, int exponent){
		int result = 1;
		while (exponent != 0){
			result *= base;
			exponent--;
		}
		return result;
	}

	public static ArrayList<Integer> primeNumbers(){
		//int [] prime = new int[1];
		ArrayList<Integer> Plist = new ArrayList<>();

		for(int i = 1; i <= 100; i++){
			int counter = 0;
			for (int num = i; num >= 1; num--){
				if (i % num == 0){
					counter++;
				}
			}
			if (counter == 2){
				//int length = Plist.lastIndexOf();
				Plist.add(i);
			}
		}
		return Plist;
	}
}
