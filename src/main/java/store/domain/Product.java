package store.domain;

import java.util.stream.Stream;

public class Product {
    private final static String LINE_SPLIT_SEPARATOR = ",";


    private final String name;
    private final int price;
    private  int quantity;
    private final Promotion promotion;

    public Product(final String name, final int price,  final int quantity, final Promotion promotion) {
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

    public Promotion getPromotion() {
        return promotion;
    }

    public String getPromotionName() {
        return promotion.getName();
    }

    public static Product createProduct(String line) {
        String[] parts = line.split(LINE_SPLIT_SEPARATOR);
        String name = parts[0];
        int price = Integer.parseInt(parts[1]);
        int quantity = Integer.parseInt(parts[2]);
        Promotion promotion = Promotions.getPromotionByName(parts[3]);

        return new Product(name, price, quantity, promotion);
    }
}
