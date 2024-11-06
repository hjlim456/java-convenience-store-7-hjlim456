package store;

import java.io.IOException;
import java.util.List;
import store.Util.FileLoader;
import store.domain.Inventory;
import store.domain.Promotion;
import store.view.OutputView;

public class StoreController {
    private static final String PROMOTIONS_FILE_ADDRESS = "src/main/resources/products.md";
    private static final String PRODUCTS_FILE_ADDRESS = "src/main/resources/products.md";
    public static void run() throws IOException {
        Inventory inventory = new Inventory("src/main/resources/products.md");
        OutputView.printInventory(inventory);

    }
}
