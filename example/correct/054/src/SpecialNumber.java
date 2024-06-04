public class SpecialNumber {
    public static boolean isSpecial(int i){
        int count = 0;
        int divisor = 2;
        int mid = i/2;

        while(i != 1 && divisor <= mid ){

            if(i % divisor == 0){
                count++;

                while(i % divisor == 0){
                    i =  i/ divisor;
                }
            }

            divisor = divisor + 1;
        }

        if(count == 3)
            return true;

        return false;
    }

    public static void main(String[] args){
        SpecialNumber s = new SpecialNumber();

        if(s.isSpecial(30))
            System.out.println("30 is a special number.");
        else
            System.out.println("30 is not a special number.");


        if(s.isSpecial(210))
            System.out.println("210 is a special number.");
        else
            System.out.println("210 is not a special number.");

    }

}
