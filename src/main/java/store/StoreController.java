package store;

import static store.constant.FileAddress.PRODUCTS_FILE_ADDRESS;
import static store.constant.FileAddress.PROMOTIONS_FILE_ADDRESS;
import static store.constant.ViewMessage.ASK_FOR_ANOTHER_PURCHASE_MESAAGE;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import store.constant.ViewMessage;
import store.domain.order.OrderParser;
import store.domain.order.OrderSheet;
import store.domain.product.FreeProducts;
import store.domain.product.FullPriceProducts;
import store.domain.product.PurchasedProducts;
import store.domain.product.Inventory;
import store.domain.discount.Promotion;
import store.domain.discount.Promotions;
import store.constant.FileAddress;
import store.view.InputView;
import store.view.OutputView;

public class StoreController {
    public static void run() throws IOException {
        Promotions promotions = new Promotions(FileAddress.getMessage(PROMOTIONS_FILE_ADDRESS));
        Inventory inventory = new Inventory(FileAddress.getMessage(PRODUCTS_FILE_ADDRESS));

        while (true) {
            OutputView.printInventory(inventory);
            receiveValidatedVoid(()->orderProcess(inventory, promotions));
            if (!askForAnotherPurchase()) break;
        }
    }

    private static void orderProcess(Inventory inventory, Promotions promotions) {
        OrderSheet orderSheet = initOrderSheet();
        List<Promotion> ongoingPromotions = promotions.getOngoingPromotions(DateTimes.now().toLocalDate());

        PurchasedProducts purchasedProducts = inventory.requestProduct(orderSheet, ongoingPromotions);
        FreeProducts freeProducts = purchasedProducts.calculateFreeProducts(ongoingPromotions);

        FullPriceProducts fullPriceProducts = purchasedProducts.calculateFullPriceProducts(freeProducts);
        purchasedProducts.askBuyFullPriceItems(fullPriceProducts);

        OutputView.printReceipt(purchasedProducts, freeProducts, fullPriceProducts);
    }

    private static OrderSheet initOrderSheet() {
       return receiveValidatedValue(() -> OrderParser.createByString(InputView.readString()));
    }

    private static boolean askForAnotherPurchase() {
        System.out.println(ViewMessage.getMessage(ASK_FOR_ANOTHER_PURCHASE_MESAAGE));
        String userInput = receiveValidatedValue(()->InputView.readYesOrNo());

        return userInput.equalsIgnoreCase("Y");
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
    private static void receiveValidatedVoid(Runnable action) {
        while (true) {
            try {
                action.run();
                return;
            } catch (IllegalArgumentException exception) {
                OutputView.printException(exception);
            }
        }
    }
}
