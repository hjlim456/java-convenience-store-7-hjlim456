package store.domain;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.domain.discount.Promotion;
import store.domain.discount.Promotions;

class PromotionsTest {
    private static final String PROMOTIONS_FILE_ADDRESS = "src/main/resources/promotions.md";

    private Promotions promotions;

    @BeforeEach
    void initProducts() {
        promotions = new Promotions(PROMOTIONS_FILE_ADDRESS);

    }
    @DisplayName("특정 날짜에 진행중인 Promotion반환 테스트")
    @Test
    void 특정_날짜에_진행중인_PROMOTION_반환_테스트() {
        //given
        LocalDate date = LocalDate.of(2024,9,13);
        //when
        List<Promotion> todayPromotions = promotions.getOngoingPromotions(date);

        List<String> ongoingPromotionNames  = todayPromotions.stream()
                .map(Promotion::getName)
                .toList();
        //then
        Assertions.assertThat(ongoingPromotionNames).containsExactlyInAnyOrder("탄산2+1","MD추천상품");
    }
}