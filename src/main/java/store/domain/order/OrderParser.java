package store.domain.order;

import static store.constant.ExceptionMessage.INVALID_ORDER;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.constant.ExceptionMessage;

public class OrderParser {
    private static final String ORDER_STRING_FORMAT = "\\[([가-힣]+)-(\\d+)\\]";
    private static final Pattern ORDER_PATTERN = Pattern.compile(ORDER_STRING_FORMAT);
    private static final String ORDER_SEPARATOR = ",";


    public static OrderSheet createByString(String orderString) {
        Map<String, Integer> orderRepository = new LinkedHashMap<>();

        for (String product : orderString.split(ORDER_SEPARATOR)) {
            Matcher matcher = createMatcher(product);
            addOrderProduct(orderRepository, matcher);
        }
        
        return new OrderSheet(orderRepository);
    }

    private static void addOrderProduct(Map<String, Integer> orderRepository, Matcher matcher) {
        if (matcher.matches()) {
            String productName = matcher.group(1);
            int quantity = Integer.parseInt(matcher.group(2));
            orderRepository.put(productName, quantity);
        }
    }

    private static Matcher createMatcher(String product) {
        Matcher matcher = ORDER_PATTERN.matcher(product);
        validateFormat(matcher);
        return matcher;
    }

    private static void validateFormat(Matcher matcher) {
        if (matcher.matches()) {
            return;
        }
        throw new IllegalArgumentException(ExceptionMessage.getMessage(INVALID_ORDER));
    }
}
