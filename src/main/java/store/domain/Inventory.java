package store.domain;

import camp.nextstep.edu.missionutils.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.Util.FileLoader;
import store.domain.discount.Promotion;

public class Inventory {
    private final static String LINE_SPLIT_SEPARATOR = ",";

    private final List<Product> totalProducts = new ArrayList<>();
    private final List<Product> promotionProducts = new ArrayList<>();
    private final List<Product> defaultProducts = new ArrayList<>();


    public Inventory(String filePath) {
        initializeFromFile(filePath);
        categorizeProduct(totalProducts);
    }

    public List<Product> getTotalProducts() {
        return totalProducts;
    }
    public List<Product> getPromotionProducts() {
        return promotionProducts;
    }
    public List<Product> getDefaultProducts() {
        return defaultProducts;
    }
    private void initializeFromFile(String filePath) {
        try {
            FileLoader.loadByFilePath(filePath).stream()
                    .skip(1)
                    .map(Product::create)
                    .forEach(product -> totalProducts.add(product));
        } catch (IOException e) {
            System.out.println("[ERROR] 파일을 읽는 도중 오류가 발생했습니다.");
        }
    }
    private void  categorizeProduct(List<Product> totalProducts) {
        for (Product product : totalProducts) {
            if(product.hasAnyPromotion()){
                promotionProducts.add(product);
            }
            defaultProducts.add(product);
        }
    }

    public List<Product> findByName(String productName) {
        List<Product> matchedProducts = totalProducts.stream()
                .filter(product -> product.getName().equals(productName))
                .toList();
        if (matchedProducts.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
        return matchedProducts;
    }

    public Map<Product, Integer> getProduct(Map<String, Integer> orderRepository, List<Promotion> todayPromotions) {
        Map<Product, Integer> purchasedProducts = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : orderRepository.entrySet()) {
            String name = entry.getKey();
            int count = entry.getValue();

            List<Product> matchedProducts = findByName(name);

            boolean hasTodayPromotion = isProductContainTodayPromotion(matchedProducts,todayPromotions);

            Map<Product, Integer> productsToAdd;

            if (hasTodayPromotion) {
                productsToAdd = getFromPromotionProductFirst(matchedProducts, count);
                productsToAdd.forEach((product, quantity) ->{
                    purchasedProducts.merge(product, quantity, Integer::sum);
                });
                continue; // 조건이 true일 경우 다음 반복으로 넘어가게하자
            }

            productsToAdd = getFromDefaultProductFirst(matchedProducts, count);
            productsToAdd.forEach((product, quantity) ->
            purchasedProducts.merge(product, quantity, Integer::sum));
        }
        return purchasedProducts;
    }

    public static boolean isProductContainTodayPromotion(List<Product> matchedProducts,List<Promotion> todayPromotions) {
        boolean hasTodayPromotion = matchedProducts.stream()
                .anyMatch(product -> todayPromotions.stream()
                        .anyMatch(promotion -> promotion.getName().equals(product.getPromotionName()))
                );
        return hasTodayPromotion;
    }

    private Map<Product, Integer> getFromPromotionProductFirst(List<Product> matchedProducts, Integer count) {
        Map<Product, Integer> purchasedProducts = new LinkedHashMap<>();

        Product promotionalProduct = matchedProducts.stream()
                    .filter(product -> !product.getPromotionName().equals("none"))
                    .findFirst()
                    .orElse(null);

        Product nonPromotionalProduct = matchedProducts.stream()
                .filter(product -> product.getPromotionName().equals("none"))
                .findFirst()
                .orElse(null);

        int remainingRequest = count;

        // 조건에 따른 추가 수량 제공 여부 확인
        if (remainingRequest < promotionalProduct.getQuantity() && (
                (promotionalProduct.getPromotionName().equals("탄산2+1") && (remainingRequest % 3 == 2)) ||
                        ((promotionalProduct.getPromotionName().equals("MD추천상품") || promotionalProduct.getPromotionName().equals("반짝할인")) && (remainingRequest % 2 == 1))
        )) {
            System.out.printf("현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)%n", promotionalProduct.getName());

            String userInput = Console.readLine();

            if (userInput.equalsIgnoreCase("Y")) {
                remainingRequest++;
            }
        }
        //행사 상품에서 차감
        int quantityToDeduct = Math.min(remainingRequest, promotionalProduct.getQuantity());
        promotionalProduct.decreaseQuantity(quantityToDeduct);
        purchasedProducts.put(promotionalProduct, quantityToDeduct);
        remainingRequest -= quantityToDeduct;

        if (remainingRequest == 0) {
            return purchasedProducts;
        }
        // 기본 상품에서 남은 수량 차감
        quantityToDeduct = Math.min(remainingRequest, nonPromotionalProduct.getQuantity());
        nonPromotionalProduct.decreaseQuantity(quantityToDeduct);
        purchasedProducts.put(nonPromotionalProduct, quantityToDeduct);
        remainingRequest -= quantityToDeduct;

        if (remainingRequest > 0) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
        return purchasedProducts;
    }

    private Map<Product, Integer> getFromDefaultProductFirst(List<Product> matchedProducts, Integer count) {
        Map<Product, Integer> purchasedProducts = new LinkedHashMap<>();

        Product promotionalProduct = matchedProducts.stream()
                .filter(product -> !product.getPromotionName().equals("none"))
                .findFirst()
                .orElse(null);

        Product nonPromotionalProduct = matchedProducts.stream()
                .filter(product -> product.getPromotionName().equals("none"))
                .findFirst()
                .orElse(null);

        int remainingRequest = count;

        // 기본 상품에서 먼저 수량 차감
        int quantityToDeduct = Math.min(remainingRequest, nonPromotionalProduct.getQuantity());
        nonPromotionalProduct.decreaseQuantity(quantityToDeduct);
        purchasedProducts.put(nonPromotionalProduct, quantityToDeduct);
        remainingRequest -= quantityToDeduct;

        if (remainingRequest == 0) {
            return purchasedProducts;
        }

        //행사 상품에서 차감
        quantityToDeduct = Math.min(remainingRequest, promotionalProduct.getQuantity());
        promotionalProduct.decreaseQuantity(quantityToDeduct);
        purchasedProducts.put(promotionalProduct, quantityToDeduct);
        remainingRequest -= quantityToDeduct;

        if (remainingRequest > 0) {
            throw new IllegalArgumentException("[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
        return purchasedProducts;
    }
}