package store.domain.product;

import camp.nextstep.edu.missionutils.Console;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import store.view.InputView;
import store.view.OutputView;

public class FullPriceProducts {
    private final Map<Product, Integer> fullPriceProducts;

    public FullPriceProducts(Map<Product, Integer> fullPriceProducts) {
        this.fullPriceProducts = fullPriceProducts;
    }

    public void forEach(BiConsumer<Product, Integer> action) {
        fullPriceProducts.forEach(action);
    }

    public void updateQuantity(Product product, int quantity) {
        fullPriceProducts.put(product, quantity);
    }

    public int getMembershipDiscount() {
        System.out.println("\n멤버십 할인을 받으시겠습니까? (Y/N)");
        String userInput = receiveValidatedValue(()-> InputView.readYesOrNo());
        if (userInput.equalsIgnoreCase("Y")) {
            int totalNonPromotionalValue = fullPriceProducts.entrySet().stream()
                    .mapToInt(entry -> entry.getKey().getPrice() * entry.getValue())
                    .sum();
            return Math.min((int) (totalNonPromotionalValue * 0.3), 8000);
        }
        return 0;
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
