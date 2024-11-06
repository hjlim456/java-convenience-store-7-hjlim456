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
        inventory.purchaseProduct("콜라", 1);
        Assertions.assertThat(inventory.getProductByName("콜라").getQuantity()).isEqualTo(9);
    }

    @DisplayName("상품 구매수량이 재고를 초과한 경우 ")
    @Test
    void 구매수량이_재고를_초과시_예외_테스트() {

        Assertions.assertThatThrownBy(() -> inventory.purchaseProduct("콜라", 100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
    }

    @DisplayName(" 상품 이름으로 Product객체 찾는 기능 확인")
    @Test
    void 존재_하는_상품_입력시_Product객체_반환_테스트() {
        String ExistsName = "콜라";
        Product product = inventory.getProductByName(ExistsName);

        Assertions.assertThat(product.getName()).isEqualTo(ExistsName);
    }

    @DisplayName("존재하지않는 상품 입력시 예외 처리 테스트")
    @Test
    void 존재_하지않는_상품_입력시_예외_테스트() {
        String notExistsName = "샤넬가방";

        Assertions.assertThatThrownBy(() -> inventory.getProductByName(notExistsName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
    }

}