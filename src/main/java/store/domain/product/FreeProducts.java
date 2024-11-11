package store.domain.product;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import store.domain.order.OrderSheet;

public class FreeProducts {
    private final Map<Product, Integer> freeProducts;

    public FreeProducts(Map<Product, Integer> freeProducts) {
        this.freeProducts = freeProducts;
    }

    public void forEachProduct(BiConsumer<Product, Integer> action) {
        freeProducts.forEach(action);
    }

    public int calculatePromotionDiscount() {
        return freeProducts.entrySet().stream()
                .mapToInt(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    public FullPriceProducts calculateFullPriceProducts(OrderSheet orderRepository) {
        Map<Product, Integer> fullPriceProduct = new LinkedHashMap<>();

        this.forEachProduct((freeProduct, freeCount) -> {
            if (orderRepository.containsKey(freeProduct.getName())) {
                int orderQuantity = orderRepository.get(freeProduct.getName());
                int fullPriceQuantity =0;

                if (freeProduct.getPromotionName().equals("탄산2+1") && orderQuantity > 3){
                    fullPriceQuantity = orderQuantity - (freeCount * 3);
                }
                if (freeProduct.getPromotionName().equals("MD추천상품")||freeProduct.getPromotionName().equals("반짝할인")
                 && orderQuantity > 2){
                    fullPriceQuantity = orderQuantity - (freeCount * 2);
                }
                if (fullPriceQuantity > 0) {
                    fullPriceProduct.put(freeProduct, fullPriceQuantity);
                }
            }
        });
        return new FullPriceProducts(fullPriceProduct);
    }
}
