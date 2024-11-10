package store;

import camp.nextstep.edu.missionutils.Console;
import camp.nextstep.edu.missionutils.DateTimes;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import store.domain.Inventory;
import store.domain.Product;
import store.domain.discount.Promotion;
import store.domain.discount.Promotions;
import store.view.OutputView;

public class StoreController {
    private static final String PROMOTIONS_FILE_ADDRESS = "src/main/resources/promotions.md";
    private static final String PRODUCTS_FILE_ADDRESS = "src/main/resources/products.md";

    private static final String ORDER_STRING_FORMAT = "\\[([가-힣]+)-(\\d+)\\]";
    private static final Pattern ORDER_PATTERN = Pattern.compile(ORDER_STRING_FORMAT);
    public static void run() throws IOException {
        Promotions promotions = new Promotions(PROMOTIONS_FILE_ADDRESS);
        Inventory inventory = new Inventory(PRODUCTS_FILE_ADDRESS);
        OutputView.printInventory(inventory);

        System.out.println("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])");
        Map<String, Integer> orderRepository = parseOrderString(Console.readLine());//품명 갯수가 들어가있다.

        LocalDate todayDate = DateTimes.now().toLocalDate(); // 오늘날짜 생성
        List<Promotion> ongoingPromotions = promotions.getOngoingPromotions(todayDate);//오늘진행중인 프로모션 가져오기

        //행사상품,기본상품 구분해서 가지고있다.
        Map<Product, Integer> purchasedProducts = inventory.sellProduct(orderRepository, ongoingPromotions);


        //증정 안내 후 증정품 갯수 담아둔곳( Product는 행사상품만 있다.)
        Map<Product, Integer> freeItems = calculateFreeItems(purchasedProducts, ongoingPromotions);

        //정가 구매해야할 목록( Product는 행사상품-갯수)
        Map<Product, Integer> fullPriceItems = calculateFullPriceProducts(orderRepository, freeItems);

        fullPriceItems.forEach((product, fullPriceQuantity) -> {
            if (fullPriceQuantity > 0) {
                System.out.printf("안내메세지 : 현재 %s %d개는 프로모션 할인이 적용되지 않습니다. 그래도 구매하시겠습니까? (Y/N)%n", product.getName(), fullPriceQuantity);
                String userInput = Console.readLine();

                if (userInput.equalsIgnoreCase("N")) {
                    // 사용자가 'N'을 선택한 경우, fullPriceQuantity만큼 purchasedProducts에서 차감
                    List<Product> matchedProducts = purchasedProducts.keySet().stream()
                            .filter(p -> p.getName().equals(product.getName()))
                            .toList();

                    int remainingToDeduct = fullPriceQuantity;

                    // 기본 상품(getPromotionName이 "none"인 상품)부터 차감
                    for (Product matchedProduct : matchedProducts) {
                        if (matchedProduct.getPromotionName().equals("none")) {
                            int availableQuantity = purchasedProducts.get(matchedProduct);
                            int quantityToDeduct = Math.min(remainingToDeduct, availableQuantity);
                            purchasedProducts.put(matchedProduct, availableQuantity - quantityToDeduct);
                            remainingToDeduct -= quantityToDeduct;
                            matchedProduct.increaseQuantity(quantityToDeduct);

                            if (remainingToDeduct == 0) break;
                        }
                    }

                    // 프로모션 상품(getPromotionName이 "none"이 아닌 상품)에서 남은 수량 차감
                    if (remainingToDeduct > 0) {
                        for (Product matchedProduct : matchedProducts) {
                            if (!"none".equals(matchedProduct.getPromotionName())) {
                                int availableQuantity = purchasedProducts.get(matchedProduct);
                                int quantityToDeduct = Math.min(remainingToDeduct, availableQuantity);
                                purchasedProducts.put(matchedProduct, availableQuantity - quantityToDeduct);
                                remainingToDeduct -= quantityToDeduct;
                                matchedProduct.increaseQuantity(quantityToDeduct);
                            }
                        }
                    }
                }
            }
        });
        System.out.println("멤버십 할인을 받으시겠습니까? (Y/N)");
        String membershipAnswer = Console.readLine();
        int membershipDiscountValue=0;

        if (membershipAnswer.equalsIgnoreCase("Y")) {
            // 프로모션이 없는 상품의 가격 * 수량 합산
            int totalNonPromotionalValue = purchasedProducts.entrySet().stream()
                    .filter(entry -> entry.getKey().getPromotionName().equals("none")) // 프로모션이 없는 상품 필터링
                    .mapToInt(entry -> entry.getKey().getPrice() * entry.getValue()) // 가격 * 수량 계산
                    .sum();

            // 30% 할인 적용 후 최대 8000원 제한
            membershipDiscountValue = (int) (totalNonPromotionalValue * 0.3);
            membershipDiscountValue = Math.min(membershipDiscountValue, 8000);
        }
    }


    public static Map<Product, Integer> calculateFullPriceProducts(Map<String, Integer> orderRepository, Map<Product, Integer> freeItems) {
        Map<Product, Integer> fullPriceProduct = new LinkedHashMap<>();

        freeItems.forEach((freeProduct, freeCount) -> {
            if (orderRepository.containsKey(freeProduct.getName())) {
                int orderQuantity = orderRepository.get(freeProduct.getName());
                int fullPriceQuantity =0;

                if (freeProduct.getPromotionName().equals("탄산2+1")){
                    fullPriceQuantity = orderQuantity - (freeCount * 3);
                }
                if (freeProduct.getPromotionName().equals("MD추천상품")||freeProduct.getPromotionName().equals("반짝할인")){
                    fullPriceQuantity = orderQuantity - (freeCount * 2);
                }
                fullPriceProduct.put(freeProduct, fullPriceQuantity);
            }
        });
        return fullPriceProduct;
    }
    public static Map<Product, Integer> calculateFreeItems(Map<Product, Integer> purchasedProducts, List<Promotion> ongoingPromotions) {
        Map<Product, Integer> freeItems = new LinkedHashMap<>();

        filterPromotionProducts(purchasedProducts).forEach(entry -> {
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
        return freeItems;
    }
    private static Stream<Entry<Product, Integer>> filterPromotionProducts(Map<Product, Integer> purchasedProducts) {
        return purchasedProducts.entrySet().stream()
                .filter(entry -> !entry.getKey().getPromotionName().equals("none"));
    }

    private static Optional<Promotion> findMatchingPromotion(Product product, List<Promotion> ongoingPromotions) {
        return ongoingPromotions.stream()
                .filter(promotion -> promotion.getName().equals(product.getPromotionName()))
                .findFirst();
    }

    private static int calculateFreeItemCount(Promotion promotion, int purchaseCount) {
        if (promotion.getName().equals("탄산2+1")) {
            return purchaseCount / 3;
        }
        if (promotion.getName().equals("반짝할인") || promotion.getName().equals("MD추천상품")) {
            return purchaseCount / 2;
        }
        return 0;
    }
    public static Map<String, Integer> parseOrderString(String orderString) {
        Map<String, Integer> orderRepository = new LinkedHashMap<>();
        for (String item : orderString.split(",")) {
            Matcher matcher = ORDER_PATTERN.matcher(item);
            if (matcher.matches()) {
                String productName = matcher.group(1);
                int quantity = Integer.parseInt(matcher.group(2));

                orderRepository.put(productName, quantity);
            } else {
                throw new IllegalArgumentException("[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.");
            }
        }
        return orderRepository;
    }
}
