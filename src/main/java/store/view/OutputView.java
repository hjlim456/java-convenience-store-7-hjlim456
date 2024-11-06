package store.view;

import java.util.stream.Stream;
import store.domain.Inventory;
import store.domain.Product;

public class OutputView {
    private final static int MIN_PRODUCT_COUNT = 1;

    public static void printInventory(Inventory inventory) {
        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        inventory.getProducts()
                .forEach(OutputView::printProduct);
    }

    private static void printProduct(Product product) {
        String quantityStatus = getQuantityStatus(product.getQuantity());
        String promotionNameInfo = getValidPromotionName(product.getPromotionName());

        System.out.printf("- %s %d원 %s %s%n",
                product.getName(), product.getPrice(), quantityStatus, promotionNameInfo);
    }

    private static String getQuantityStatus(int quantity) {
        return Stream.of(quantity)
                .filter(q -> q >= MIN_PRODUCT_COUNT)
                .map(q -> q + "개")
                .findFirst()
                .orElse("재고없음");
    }

    private static String getValidPromotionName(String promotionName) {
        return Stream.of(promotionName)
                .filter(name -> !name.equals("none"))
                .findFirst()
                .orElse("");
    }
}
