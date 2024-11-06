package store.domain;

public class Product {
    private final static String LINE_SPLIT_SEPARATOR = ",";

    private final String name;
    private final int price;
    private  int quantity;
    private final String promotionName;

    public Product(final String name, final int price,  final int quantity, final String promotionName) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.promotionName = promotionName;
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

    public String getPromotionName() {
        return promotionName;
    }


    public static Product create(String line) {
        String[] parts = line.split(LINE_SPLIT_SEPARATOR);
        String name = parts[0];
        int price = Integer.parseInt(parts[1]);
        int quantity = Integer.parseInt(parts[2]);
        String promotionName = parts[3];

        return new Product(name, price, quantity, promotionName);
    }

    public void decreaseQuantity(int count) {
        //재고 수량은 행사상품, 기본상품 추가한 수량인디?
        if (quantity < count) {
            throw  new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }

        quantity -= count;
    }
}
