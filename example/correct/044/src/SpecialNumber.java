
public class SpecialNumber {
	public static void main(String[] args){
		System.out.println(isSpecial(30));
	}

	public static boolean isSpecial(int num) {
		double num1 = (double) num;
		int count = 0;

		for(int i =2; i < Math.sqrt(num1); i++){
			if (num % i == 0){
				count++;
				num = num / i;
				if (num % i == 0){
					num = num / i;
					if(num % i == 0){
						num = num / i;
						if (num % i == 0){
							num = num / i;
							if (num % i == 0){
								num = num / i;
								if (num % i == 0){
									num = num / i;
									if (num % i == 0){
										num = num / i;
										if (num % i == 0){
											num = num / i;
											if (num % i == 0){
												num = num / i;
												if (num % i == 0){
													num = num / i;
													if (num % i == 0){
														num = num / i;
														if (num % i == 0){
															num = num / i;
															if (num % i == 0){
																num = num / i;
																if (num % i == 0){
																	num = num / i;
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}

					}
				}
			}
		}
		if (count == 3){return true;}
		else {return false;}
	}}




