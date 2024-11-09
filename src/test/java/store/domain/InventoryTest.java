package store.domain;

import static store.StoreController.parseOrderString;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import store.domain.discount.Promotion;
import store.domain.discount.Promotions;

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

//    @DisplayName("프로모션 기간중 상품 구매시  프로모션재고가 차감됨을 확인한다.")
//    @Test
//    void 프로모션_기간중_해당_상품_구매시_프로모션재고_차감_테스트() {
//        //given
//        Map<String, Integer> orderRepository = parseOrderString("[콜라-4],[감자칩-3]");//품명 갯수가 들어가있다.
//
//        LocalDate todayDate = LocalDate.of(2024,9,9); // 오늘날짜 생성
//        List<Promotion> todayOngoingPromotions = promotions.getOngoingPromotions(todayDate);//오늘진행중인 프로모션 가져오기
//        //when
//        Map<Product, Integer> purchasedProducts = inventory.getProduct(orderRepository, todayOngoingPromotions);
//
//        //then
//        Assertions.assertThat(inventory.getProductByName("콜라").getQuantity()).isEqualTo(9);
//    }

//    @DisplayName("프로모션 기간이 아닐때 상품 구매시 일반재고가 차감됨을 확인한다.")
//    @Test
//    void 프로모션_기간이_아닐떄_상품_구매시_일반재고가_차감_테스트() {
//        inventory.sellProduct("콜라", 1);
//        Assertions.assertThat(inventory.getProductByName("콜라").getQuantity()).isEqualTo(9);
//    }

//    @DisplayName("상품 구매수량이 재고를 초과한 경우 ")
//    @Test
//    void 구매수량이_재고를_초과시_예외_테스트() {
//
//        Assertions.assertThatThrownBy(() -> inventory.sellProduct("콜라", 100))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
//    }

    @DisplayName(" 상품 이름으로 Product객체 찾는 기능 확인")
    @Test
    void 존재_하는_상품_입력시_해당_Product_객체_반환_테스트() {
        String ExistsName = "콜라";
        List<Product> matchProduct = inventory.findByName(ExistsName);

        Assertions.assertThat(matchProduct)
                .allMatch(product -> product.getName().equals(ExistsName));    }

    @DisplayName("존재하지않는 상품 입력시 예외 처리 테스트")
    @Test
    void 존재_하지않는_상품_입력시_예외_테스트() {
        String notExistsName = "샤넬가방";

        Assertions.assertThatThrownBy(() -> inventory.findByName(notExistsName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
    }

    @DisplayName("검색한 상품이 오늘 프로모션을 갖고있는지 확인한다.")
    @ParameterizedTest
    @MethodSource("provideProductNamesAndExpectedPromotionStatus")
    void 검색한_상품이_오늘_프로모션을_갖고있는지_확인한다(String productName, boolean expectedPromotionStatus) {
        //given
        LocalDate date = LocalDate.of(2024,7,3);
        //when
        List<Product> matchProduct = inventory.findByName(productName);
        List<Promotion> todayOngoingPromotions = promotions.getOngoingPromotions(date);
        boolean productContainTodayPromotion = inventory.isProductContainTodayPromotion(matchProduct,todayOngoingPromotions);
        //then
        Assertions.assertThat(productContainTodayPromotion).isEqualTo(expectedPromotionStatus);
    }
    private static Stream<Arguments> provideProductNamesAndExpectedPromotionStatus() {
        return Stream.of(
                Arguments.of("감자칩", false),
                Arguments.of("콜라", true),
                Arguments.of("사이다", true)
        );
    }
}