package store.view;

import java.util.stream.Stream;
import store.domain.Inventory;
import store.domain.Product;

public class OutputView {

    public static void printInventory(Inventory inventory) {
        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        inventory.getProducts()
                .forEach(OutputView::printProduct);
    }
    private static void printProduct(Product product) {
        String quantityInfo = product.getQuantityInfo(product.getQuantity());
        String promotionNameInfo = product.getPromotionNameInfo(product.getPromotionName());

        System.out.printf("- %s %d원 %s %s%n",
                product.getName(), product.getPrice(), quantityInfo, promotionNameInfo);
    }

}
