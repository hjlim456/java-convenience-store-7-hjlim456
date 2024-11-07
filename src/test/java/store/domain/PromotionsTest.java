package store.domain;

import static org.junit.jupiter.api.Assertions.*;

import camp.nextstep.edu.missionutils.DateTimes;
import java.time.LocalDate;
import java.util.List;
import javax.xml.stream.events.DTD;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        List<Promotion> todayPromotions = promotions.getTodayPromotions(date);

        List<String> ongoingPromotionNames  = todayPromotions.stream()
                .map(Promotion::getName)
                .toList();
        //then
        Assertions.assertThat(ongoingPromotionNames).containsExactlyInAnyOrder("탄산2+1","MD추천상품","none");
    }
}