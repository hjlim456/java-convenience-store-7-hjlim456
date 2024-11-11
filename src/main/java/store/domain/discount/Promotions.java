package store.domain.discount;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import store.Util.FileLoader;

public class Promotions {
    private final List<Promotion> promotions = new ArrayList<>();

    public Promotions(String filePath) {
        initializeFromFile(filePath);
    }

    public Promotion getPromotionByName(String input) {
        return promotions.stream()
                .filter(promotion -> promotion.getName().equals(input))
                .findFirst()
                .orElseGet(()-> {
                    throw new IllegalArgumentException("존재하지않는 프로모션입니다");
                });
    }
    private void initializeFromFile(String filePath) {
        try {
            FileLoader.loadByFilePath(filePath).stream()
                    .skip(1)
                    .map(Promotion::create)
                    .forEach(promotions::add);
        } catch (IOException e) {
            System.out.println("[ERROR] 파일을 읽는 도중 오류가 발생했습니다.");
        }
    }
    public List<Promotion> getOngoingPromotions(LocalDate nowDate) {
        return promotions.stream()
                .filter(promotion -> isWithinPromotionPeriod(promotion, nowDate))
                .filter(promotion -> !promotion.getName().equals("none")) // "none" 이름 제외
                .toList();
    }

    public boolean isWithinPromotionPeriod(Promotion promotion, LocalDate nowDate) {
        return !nowDate.isBefore(promotion.getStartDate()) && !nowDate.isAfter(promotion.getEndDate());
    }
}
