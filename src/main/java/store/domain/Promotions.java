package store.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import store.Util.FileLoader;

public class Promotions {
    private static final List<Promotion> PROMOTIONS = new ArrayList<>();

    public Promotions(String filePath) {
        initializeFromFile(filePath);
    }

    public static Promotion getPromotionByName(String input) {
        return PROMOTIONS.stream()
                .filter(promotion -> promotion.getName().equals(input))
                .findFirst()
                .orElse(null);
    }
    private void initializeFromFile(String filePath) {
        try {
            FileLoader.loadByFilePath(filePath).stream()
                    .skip(1)
                    .map(Promotion::createPromotion)
                    .forEach(PROMOTIONS::add);
        } catch (IOException e) {
            System.out.println("[ERROR] 파일을 읽는 도중 오류가 발생했습니다.");
        }
    }
}
