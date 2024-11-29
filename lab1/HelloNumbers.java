public class HelloNumbers {
    public static void main(String[] args) {
        int x = 1;
        int total = 0;
        System.out.println(Collatz.nextNumber(7));
        while (x <= 10) {
            System.out.print(total + " ");
            total = total + x;
            x = x + 1;
        }
	}
} 
