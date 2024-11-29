public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        int[] ages = new int[5];
        ages[0] = 20;
        for (int i = 1; i < ages.length; i++) {
            System.out.println("Age: " + ages[i - 1]);
        }
    }
} 
