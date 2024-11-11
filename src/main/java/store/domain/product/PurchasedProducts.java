package store.domain.product;

import camp.nextstep.edu.missionutils.Console;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import store.domain.discount.Promotion;
import store.domain.order.OrderSheet;
import store.view.InputView;
import store.view.OutputView;

public class PurchasedProducts {
    private final Map<Product, Integer> purchasedProducts;

    public PurchasedProducts(Map<Product, Integer> purchasedProducts) {
        this.purchasedProducts = purchasedProducts;
    }

    public void forEachProduct(BiConsumer<Product, Integer> action) {
        purchasedProducts.forEach(action);
    }

    public Stream<Entry<Product, Integer>> filterPromotionProducts() {
        return purchasedProducts.entrySet().stream()
                .filter(entry -> !entry.getKey().getPromotionName().equals("none"));
    }

    public FreeProducts calculateFreeProducts(List<Promotion> ongoingPromotions) {
        Map<Product, Integer> freeItems = new LinkedHashMap<>();


        this.filterPromotionProducts().forEach(entry -> {
            Product product = entry.getKey();
            int purchaseCount = entry.getValue();

            findMatchingPromotion(product, ongoingPromotions)
                    .ifPresent(promotion -> {
                        int freeCount = calculateFreeItemCount(promotion, purchaseCount);
                        if (freeCount > 0) {
                            freeItems.put(product, freeCount);
                        }
                    });
        });
        return new FreeProducts(freeItems);
    }

    private Optional<Promotion> findMatchingPromotion(Product product, List<Promotion> ongoingPromotions) {
        return ongoingPromotions.stream()
                .filter(promotion -> promotion.getName().equals(product.getPromotionName()))
                .findFirst();
    }

    private int calculateFreeItemCount(Promotion promotion, int purchaseCount) {
        if (promotion.getName().equals("탄산2+1")) {
            return purchaseCount / 3;
        }
        if (promotion.getName().equals("반짝할인") || promotion.getName().equals("MD추천상품")) {
            return purchaseCount / 2;
        }
        return 0;
    }


    public void askBuyFullPriceItems(FullPriceProducts fullPriceItems) {
        fullPriceItems.forEach((product, fullPriceQuantity) -> {
            if (fullPriceQuantity > 0 && product.hasAnyPromotion()) {
                System.out.printf("현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)%n", product.getName(), fullPriceQuantity);

                String userInput = receiveValidatedValue(()->InputView.readYesOrNo());
                if (userInput.equalsIgnoreCase("N")) {
                    reduceFullPriceProducts(product, fullPriceQuantity);
                    fullPriceItems.updateQuantity(product, 0);
                }
            }
        });
    }

    private void reduceFullPriceProducts(Product product, int fullPriceQuantity) {
        List<Product> matchedProducts = purchasedProducts.keySet().stream()
                .filter(p -> p.getName().equals(product.getName()))
                .toList();

        int remainingToDeduct = fullPriceQuantity;

        for (Product matchedProduct : matchedProducts) {
            if (matchedProduct.getPromotionName().equals("none")) {
                remainingToDeduct = adjustProductQuantity(purchasedProducts, matchedProduct, remainingToDeduct);
                if (remainingToDeduct == 0) break;
            }
        }

        if (remainingToDeduct > 0) {
            for (Product matchedProduct : matchedProducts) {
                if (!matchedProduct.getPromotionName().equals(("none"))) {
                    remainingToDeduct = adjustProductQuantity(purchasedProducts, matchedProduct, remainingToDeduct);
                }
            }
        }
    }

    private int adjustProductQuantity(Map<Product, Integer> purchasedProducts, Product product, int remainingToDeduct) {
        int availableQuantity = purchasedProducts.get(product);
        int quantityToDeduct = Math.min(remainingToDeduct, availableQuantity);
        purchasedProducts.put(product, availableQuantity - quantityToDeduct);
        product.increaseQuantity(quantityToDeduct);
        return remainingToDeduct - quantityToDeduct;
    }

    public int calculateTotalAmount() {
        return purchasedProducts.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }
    public int calculateTotalQuantity() {
        return purchasedProducts.values().stream().mapToInt(Integer::intValue).sum();
    }


    public Map<String, Integer> calculateAggregatedQuantities() {
        Map<String, Integer> quantities = new LinkedHashMap<>();
        this.forEachProduct((product, quantity) ->
                quantities.merge(product.getName(), quantity, Integer::sum)
        );
        return quantities;
    }

    public Map<String, Integer> calculateAggregatedPrices() {
        Map<String, Integer> prices = new LinkedHashMap<>();
        this.forEachProduct((product, quantity) ->
                prices.merge(product.getName(), product.getPrice() * quantity, Integer::sum)
        );
        return prices;
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
    public FullPriceProducts calculateFullPriceProducts(FreeProducts freeProducts) {
        Map<String, Integer> aggregatedQuantities = new LinkedHashMap<>();

        // "none" 프로모션인 경우 그대로 fullPriceProduct에 추가
        purchasedProducts.forEach((product, quantity) -> {
            int fullPriceQuantity = 0;

            if (product.getPromotionName().equals("none")) {
                fullPriceQuantity = quantity;
            }
            if (!product.getPromotionName().equals("none")){
                int freeCount = freeProducts.getFreeCount(product); // freeItem 갯수를 가져옴

                // 프로모션 이름에 따라 fullPriceQuantity 계산
                if (product.getPromotionName().equals("탄산2+1")) {
                    fullPriceQuantity = quantity - (freeCount * 3);
                }
                if (product.getPromotionName().equals("반짝할인") || product.getPromotionName().equals("MD추천상품")) {
                    fullPriceQuantity = quantity - (freeCount * 2);
                }
            }

            // fullPriceQuantity가 0보다 클 때만 추가
            if (fullPriceQuantity > 0) {
                aggregatedQuantities.merge(product.getName(), fullPriceQuantity, Integer::sum);
            }
        });

        // 최종적으로 Product 객체와 합산된 수량을 fullPriceProduct에 추가
        Map<Product, Integer> fullPriceProduct = new LinkedHashMap<>();
        aggregatedQuantities.forEach((productName, totalQuantity) -> {
            // productName에 해당하는 Product 객체를 가져와 fullPriceProduct에 추가
            purchasedProducts.keySet().stream()
                    .filter(product -> product.getName().equals(productName))
                    .findFirst()
                    .ifPresent(product -> fullPriceProduct.put(product, totalQuantity));
        });

        return new FullPriceProducts(fullPriceProduct);
    }
}
