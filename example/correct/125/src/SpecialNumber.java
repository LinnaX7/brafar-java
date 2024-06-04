public class SpecialNumber{

    public static boolean isSpecial(int i){

        int count = 0;


        int divisor = 2;


        int mid = i/2;


        while(i!=1 && divisor <= mid ){

            if(i%divisor==0){
                count++;
                while(i%divisor==0){
                    i = i/ divisor;
                }
            }

            divisor++;
        }


        if(count == 3)
            return true;

        return false;
    }


}
