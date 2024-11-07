package store;

import camp.nextstep.edu.missionutils.Console;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import store.domain.Inventory;
import store.domain.Product;
import store.domain.Promotions;
import store.view.OutputView;

public class StoreController {
    private static final String PROMOTIONS_FILE_ADDRESS = "src/main/resources/promotions.md";
    private static final String PRODUCTS_FILE_ADDRESS = "src/main/resources/products.md";
    public static void run() throws IOException {
        Promotions promotions = new Promotions(PROMOTIONS_FILE_ADDRESS);
        Inventory inventory = new Inventory(PRODUCTS_FILE_ADDRESS);
        OutputView.printInventory(inventory);

        System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        String orderString = Console.readLine();
        Map<String, Integer> order = parseOrderString(orderString);
        System.out.println(order.get("콜라"));

    }

    private static Map<String, Integer> parseOrderString(String orderString) {
        Map<String, Integer> orderRepository = new HashMap<>();

        for (String item : orderString.split(",")) {
            item = item.replaceAll("[\\[\\]]", "");
            String[] parts = item.split("-");

            String productName = parts[0];
            int quantity = Integer.parseInt(parts[1]);

            orderRepository.put(productName, quantity);
        }
        return orderRepository;
    }
}
