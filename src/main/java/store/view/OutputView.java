package store.view;

import store.domain.Inventory;
import store.domain.Product;

public class OutputView {
    public static void printInventory(Inventory inventory) {
        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        inventory.getProducts()
                .forEach(OutputView::printProduct);
    }

    private static void printProduct(Product product) {
        System.out.printf("- %s %d원 %s개 %s%n",
                product.getName(), product.getPrice(), product.getQuantity(), product.getPromotion());
    }
}
