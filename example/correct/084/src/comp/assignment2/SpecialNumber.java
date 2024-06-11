package comp.assignment2;

public class SpecialNumber {

    public static boolean isSpecial(int num) {
        // Task 3: Return true if and only if 'num' is special
        int count = 0;
        if (num <= 0) {
            return false;
        }
        String store = "";
        for (int i = 2; i <= num / 2; i++) {
            count = 0;
            for (int u = 1; u <= i / 2; u++) {
                if (i % u == 0) {
                    count++;
                }
            }
            if (count == 1) {
                store = store + String.valueOf(i) + ",";
            }
        }
        store = store.substring(0, store.length() - 1);
        String[] array = store.split(",");
        int[] primenumbers = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            primenumbers[i] = Integer.parseInt(array[i]);
        }
        for (int i = 0; i < primenumbers.length; i++) {
            for (int y = i + 1; y < primenumbers.length; y++) {
                for (int k = y + 1; k < primenumbers.length; k++) {
                    if (primenumbers[i] * primenumbers[y] * primenumbers[k] == num) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
