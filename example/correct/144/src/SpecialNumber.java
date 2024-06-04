
public class SpecialNumber {
	public static void main(String[] args){
		System.out.println(isSpecial(30));
	}

	public static boolean isSpecial(int num) {
		// Task 3: Return true i f and only if 'num' is special
		if (num >= 30) {
			boolean[] composite = new boolean[num + 1];
			composite[0] = true;
			composite[1] = true;
			int x = 0;
			int y = num;

			for (int p = 2; p <= num; p++) {
				if (!composite[p]) {
					for (int multiplesP = 2 * p; multiplesP <= num; multiplesP += p) {
						composite[multiplesP] = true;
					}
				}
			}
			for (int i = 0; i <= num; i++) {
				if (!composite[i]) {
					if (y % i == 0) {
						y = y / i;
						x++;
					}
				}
			}

			return x == 3;
		}
		else {
			return false;
		}
	}
}