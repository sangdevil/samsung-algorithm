package javaTest;

public class Cart {

    private int cartNumber;

    public int count;

    Cart(int cartNumber) {
      this.cartNumber = cartNumber;
      this.count = 0;
    }

    public int getCartNumber() {
        return cartNumber;
    }

    public static int getCartVersion() {
        return 124;
    }
}
