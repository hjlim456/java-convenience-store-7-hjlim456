package store;

import java.io.IOException;
import store.domain.Inventory;
import store.domain.Promotions;
import store.view.OutputView;

public class StoreController {
    private static final String PROMOTIONS_FILE_ADDRESS = "src/main/resources/promotions.md";
    private static final String PRODUCTS_FILE_ADDRESS = "src/main/resources/products.md";
    public static void run() throws IOException {
        Promotions promotions = new Promotions(PROMOTIONS_FILE_ADDRESS);
        Inventory inventory = new Inventory(PRODUCTS_FILE_ADDRESS);
        OutputView.printInventory(inventory);

    }
}
