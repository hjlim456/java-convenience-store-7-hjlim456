package store.domain;

public class Product {
    private final static String LINE_SPLIT_SEPARATOR = ",";

    private final String name;
    private final int price;
    private  int quantity;
    private final String promotion;


    public Product(final String name, final int price,  final int quantity, final String promotion) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotion = promotion;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPromotion() {
        return promotion;
    }

    public static Product createProduct(String line) {
        String[] parts = line.split(LINE_SPLIT_SEPARATOR);
        String name = parts[0];
        int price = Integer.parseInt(parts[1]);
        int quantity = Integer.parseInt(parts[2]);
        String promotion = parts[3];

        return new Product(name, price, quantity, promotion);
    }
}
