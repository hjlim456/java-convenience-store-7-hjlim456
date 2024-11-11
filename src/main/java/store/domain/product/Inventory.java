package store.domain.product;

import camp.nextstep.edu.missionutils.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import store.Util.FileLoader;
import store.constant.ViewMessage;
import store.domain.discount.Promotion;
import store.domain.order.OrderSheet;
import store.view.InputView;
import store.view.OutputView;

public class Inventory {
    private final static String LINE_SPLIT_SEPARATOR = ",";

    private final List<Product> totalProducts = new ArrayList<>();


    public Inventory(String filePath) {
        initializeFromFile(filePath);
    }

    public List<Product> getTotalProducts() {
        return totalProducts;
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

    public List<Product> findByName(String productName) {
        List<Product> matchedProducts = totalProducts.stream()
                .filter(product -> product.getName().equals(productName))
                .toList();
        if (matchedProducts.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
        return matchedProducts;
    }

    public PurchasedProducts requestProduct(OrderSheet orderRepository, List<Promotion> todayPromotions) {
        Map<Product, Integer> purchasedProducts = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : orderRepository.entrySet()) {
            String requestProductname = entry.getKey();
            int requestProductcount = entry.getValue();

            List<Product> matchedProducts = findByName(requestProductname);
            LinkedHashMap<Product, Integer> gottedProducts = getProductsByPromotionPreference(todayPromotions, matchedProducts,
                    requestProductcount);
            gottedProducts.forEach((product, quantity) -> purchasedProducts.merge(product, quantity, Integer::sum));
        }
        return new PurchasedProducts(purchasedProducts);
    }

    private LinkedHashMap<Product, Integer> getProductsByPromotionPreference(List<Promotion> todayPromotions, List<Product> matchedProducts, int requestProductcount) {
        Map<Product, Integer> productsToAdd;

        if (isProductContainTodayPromotion(matchedProducts, todayPromotions)) {
            productsToAdd = getFromPromotionProductFirst(matchedProducts, requestProductcount);
            return new LinkedHashMap<>(productsToAdd);
        }

        productsToAdd = getFromDefaultProductFirst(matchedProducts, requestProductcount);
        return new LinkedHashMap<>(productsToAdd);
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
        Product promotionalProduct = findProductByPromotion(matchedProducts, true);
        Product nonPromotionalProduct = findProductByPromotion(matchedProducts, false);
        int remainingRequest = offerPromotionOption(promotionalProduct, count);
        remainingRequest = deductQuantity(promotionalProduct, remainingRequest, purchasedProducts);//행사상품에서차감
        if (remainingRequest == 0) {
            return purchasedProducts;
        }
        remainingRequest = deductQuantity(nonPromotionalProduct, remainingRequest, purchasedProducts);//기본상품에서차감
        validateRemainingStock(remainingRequest);
        return purchasedProducts;
    }

    private int deductQuantity(Product product, int requestCount, Map<Product, Integer> purchasedProducts) {
        if (product == null) return requestCount;
        int quantityToDeduct = Math.min(requestCount, product.getQuantity());

        product.decreaseQuantity(quantityToDeduct);
        purchasedProducts.put(product, quantityToDeduct);

        return requestCount - quantityToDeduct;
    }

    private Product findProductByPromotion(List<Product> matchedProducts, boolean hasPromotion) {
        return matchedProducts.stream()
                .filter(product -> hasPromotion == !product.getPromotionName().equals("none"))
                .findFirst()
                .orElse(null);
    }

    private static int offerPromotionOption(Product promotionalProduct,Integer count) {
        int remainingRequest = count;
        if (canProvideBonusQuantity(promotionalProduct, remainingRequest)) {
            System.out.printf(ViewMessage.getMessage(ViewMessage.INPUT_ASK_FOR_ADD_FREE_PRODUCT), promotionalProduct.getName());

            String userInput = receiveValidatedValue(()->InputView.readYesOrNo());
            if (userInput.equalsIgnoreCase("Y")) {
                remainingRequest++;
            }
        }
        return remainingRequest;
    }

    private static boolean canProvideBonusQuantity(Product promotionalProduct, int remainingRequest) {
        return remainingRequest <= promotionalProduct.getQuantity() && (
                (promotionalProduct.getPromotionName().equals("탄산2+1") && (remainingRequest % 3 == 2)) ||
                        ((promotionalProduct.getPromotionName().equals("MD추천상품")
                                || promotionalProduct.getPromotionName().equals("반짝할인")) && (remainingRequest % 2 == 1))
        );
    }

    private  Map<Product, Integer> getFromDefaultProductFirst(List<Product> matchedProducts, Integer count) {
        Map<Product, Integer> purchasedProducts = new LinkedHashMap<>();
        Product promotionProduct = findProductByPromotion(matchedProducts, true);
        Product defaultProduct = findProductByPromotion(matchedProducts, false);
        int remainingRequest = count;
        remainingRequest = deductQuantity(defaultProduct, remainingRequest, purchasedProducts);
        if (remainingRequest == 0) {
            return purchasedProducts;
        }
        remainingRequest = deductQuantity(promotionProduct, remainingRequest, purchasedProducts);
        validateRemainingStock(remainingRequest);
        return purchasedProducts;
    }

    private static void validateRemainingStock(int remainingRequest) {
        if (remainingRequest > 0) {
            throw new IllegalArgumentException("재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요.");
        }
    }

    private static <T> T receiveValidatedValue(Supplier<T> inputMethod) {
        while (true) {
            try {
                return inputMethod.get();
            } catch (IllegalArgumentException exception) {
                OutputView.printException(exception);
            }
        }
    }
}