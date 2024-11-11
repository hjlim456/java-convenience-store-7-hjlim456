package store.domain.product;

import java.util.LinkedHashMap;
import java.util.List;
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
    public int getFreeCount(Product product) {
        return freeProducts.getOrDefault(product, 0); // 없으면 0 반환
    }


}
