package store.view;

import static store.constant.ExceptionMessage.MESSAGE_PREFIX;

import java.text.DecimalFormat;
import java.util.Map;
import store.domain.product.FreeProducts;
import store.domain.product.FullPriceProducts;
import store.domain.product.PurchasedProducts;
import store.domain.product.Inventory;
import store.domain.product.Product;
import store.constant.ExceptionMessage;

public class OutputView {
    private final static int MIN_PRODUCT_COUNT = 1;
    private final static String DEFAULT_PROMOTION_NAME = "none";
    private final static String DEFAULT_PROMOTION_NAME_OUTPUT_FORMAT = "";
    public static final DecimalFormat OUTPUT_FORMAT_MONEY = new DecimalFormat("#,###");

    public static void printException(IllegalArgumentException exception) {
        System.out.println(ExceptionMessage.getMessage(MESSAGE_PREFIX) + exception.getMessage());
    }

    public static void printInventory(Inventory inventory) {
        System.out.println();
        System.out.println("안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n");
        inventory.getTotalProducts()
                .forEach(OutputView::printProduct);
    }

    private static void printProduct(Product product) {
        String quantityStatus = getQuantityStatus(product.getQuantity());
        String promotionName = getPromotionNameOrDefault(product.getPromotionName());

        System.out.printf("- %s %s %s %s%n",
                product.getName(), OUTPUT_FORMAT_MONEY.format(product.getPrice()), quantityStatus, promotionName);
    }

    private static String getQuantityStatus(int quantity) {
        if (quantity >= MIN_PRODUCT_COUNT) {
            return quantity + "개";
        }
        return "재고 없음";
    }

    private static String getPromotionNameOrDefault(String promotionName) {
        if (!promotionName.equals(DEFAULT_PROMOTION_NAME)) {
            return promotionName;
        }
        return DEFAULT_PROMOTION_NAME_OUTPUT_FORMAT;
    }

    public static void printReceipt(PurchasedProducts purchasedProducts, FreeProducts freeProducts, FullPriceProducts fullPriceProducts) {
        int promotionDiscountAmount = freeProducts.calculatePromotionDiscount();
        int membershipDiscountAmount = fullPriceProducts.getMembershipDiscount();
        int totalQuantity = purchasedProducts.calculateTotalQuantity();
        int totalAmount = purchasedProducts.calculateTotalAmount();
        int finalAmount = totalAmount - promotionDiscountAmount - membershipDiscountAmount;

        printReceiptDetail(purchasedProducts, freeProducts, totalAmount, totalQuantity, promotionDiscountAmount,
                membershipDiscountAmount, finalAmount);
    }

    private static void printReceiptDetail(PurchasedProducts purchasedProducts, FreeProducts freeProducts, int totalAmount,
                                  int totalQuantity, int promotionDiscountAmount, int membershipDiscountAmount,
                                  int finalAmount) {
        System.out.println();
        System.out.println("==============W 편의점================");
        printPurchasedProducts(purchasedProducts);
        printFreeItems(freeProducts);
        printTotalAmount(totalAmount, totalQuantity);
        printPromotionDiscount(promotionDiscountAmount);
        printMembershipDiscount(membershipDiscountAmount);
        printFinalAmount(finalAmount);
        System.out.println();
    }

    private static void printPurchasedProducts(PurchasedProducts purchasedProducts) {
        System.out.println("상품명\t수량\t금액");

        Map<String, Integer> aggregatedQuantities = purchasedProducts.calculateAggregatedQuantities();
        Map<String, Integer> aggregatedPrices = purchasedProducts.calculateAggregatedPrices();

        aggregatedQuantities.forEach((name, quantity) -> {
            int totalAmount = aggregatedPrices.get(name);
            System.out.printf("%s\t\t%d\t\t%s%n", name, quantity, OUTPUT_FORMAT_MONEY.format(totalAmount));
        });
    }

    private static void printFreeItems(FreeProducts freeItems) {
        System.out.println("=============증\t정===============");
        freeItems.forEachProduct((product, quantity) -> {
            System.out.printf("%s\t\t%d%n", product.getName(), quantity);
        });
    }

    private static void printTotalAmount(int totalAmount,int totalQuantity) {
        System.out.println("==================================");
        System.out.printf("총구매액\t%d\t%s%n", totalQuantity,OUTPUT_FORMAT_MONEY.format(totalAmount));
    }

    private static void printPromotionDiscount(int promotionDiscount) {
        System.out.printf("행사할인\t\t\t-%s%n", OUTPUT_FORMAT_MONEY.format(promotionDiscount));
    }

    private static void printMembershipDiscount(int membershipDiscount) {
        System.out.printf("멤버십할인\t\t\t-%s%n", OUTPUT_FORMAT_MONEY.format(membershipDiscount));
    }

    private static void printFinalAmount(int finalAmount) {
        System.out.printf("내실돈\t\t\t%s%n", OUTPUT_FORMAT_MONEY.format(finalAmount));
    }

}
