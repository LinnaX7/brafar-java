
public class SpecialNumber {
	public static int run(int n,int p,int t){
		//System.out.print("n:");
		//System.out.print(n);
		//System.out.print("p:");
		//System.out.print(p);
		//System.out.print("t:");
		//System.out.println(t);
		if(n==1&&t==3){
			return 1;
		}else if(n==1){
			return 0;
		}
		else if(t>3||p>n){
			return -1;
		}else{
			if(n%p==0){
				t+=1;
			}
			while (true){
				if(n%p==0) {
					n = n / p;
				}else {
					break;
				}
			}
			while (true){
				int j=0;
				p+=1;
				//System.out.print("p:");
				//System.out.println(p);
				for(int i=2;i<p;i++){
					if(p%i==0){
						j=1;
						//System.out.print("i:");
						//System.out.println(i);
						break;
					}
				}
				if(j==0){
					break;
				}
			}
			//System.out.println("OK2");
		}
		return run(n,p,t);
	}
	public static boolean isSpecial(int num) {
		// Task 3: Return true if and only if 'num' is special
		int r=0;
		r=run(num,2,0);
		if(r==1){
			return true;
		}else {
			return false;
		}
	}


}
