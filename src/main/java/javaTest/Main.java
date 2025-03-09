package javaTest;

public class Main {
    public static final int finalV = 1024;

    public static int staticV = 1024;

    public int publicV = 1024;

    private int privateV = 1024;

    public static void main(String[] args) {

        int normalV = 0;
        Cart cart = new Cart(10);
        int cartNumber = cart.getCartNumber();
        int cartVersion = Cart.getCartVersion();
        int cartCount = cart.count++;
        System.out.println(String.format("version : %d, number : %d", cartNumber, cartVersion));


    }
}
