package comp.assignment2;

import java.util.Scanner;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int[] arr1 = new int[20];
        int[] arr2 = new int[20];
        int index1 = 0, index2 = 0, count = 0;
        int num_copy = num;
        if (isPrime(num) == false && num > 1) {
            // num should not be a prime number and should be greater than 1
            for (int i = 2; i < num_copy; i++) {
                while (num % i == 0) {
                    // finding the factors of num
                    arr1[index1] = i;
                    num = num / i;
                    index1++;
                }
            }
            // Finding the duplicate factors and the prime factors
            for (int j : arr1) {
                int rec = 0;
                for (int k : arr2) {
                    if (k == j) {
                        rec = 1;
                        break;
                    }
                }
                if (rec == 0 && isPrime(j) == true) {
                    count++;
                    arr2[index2] = j;
                    index2++;
                }
            }
            if (count == 3) {
                // If there are exactly three different prime factors
                return true;
            }
        }
        return false;
    }

    public static boolean isPrime(int num) {
        // function for determining whether a number is prime or not
        if (num == 2) {
            return true;
        }
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
