package store.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InventoryTest {
    private static final String PROMOTIONS_FILE_ADDRESS = "src/main/resources/promotions.md";
    private static final String PRODUCTS_FILE_ADDRESS = "src/main/resources/products.md";

    private Inventory inventory;
    private Promotions promotions;

    @BeforeEach
    void initProducts() {
        inventory = new Inventory(PRODUCTS_FILE_ADDRESS);
        promotions = new Promotions(PROMOTIONS_FILE_ADDRESS);

    }

    @DisplayName("상품 구매시 Inventory의 재고가 차감됨을 확인한다.")
    @Test
    void 상품_구매시_재고_차감_테스트() {
        inventory.deductQuantity("콜라", 1);
        Assertions.assertThat(inventory.getProductByName("콜라").getQuantity()).isEqualTo(9);
    }

}