package store.domain.order;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class OrderSheet {
    private final Map<String, Integer> orderSheet;

    public OrderSheet(Map<String, Integer> orderSheet) {
        this.orderSheet = orderSheet;
    }

    public Set<Entry<String, Integer>> entrySet() {
        return orderSheet.entrySet();
    }

    public boolean containsKey(String productName) {
        return orderSheet.containsKey(productName);
    }

    public int get(String productName) {
        return orderSheet.get(productName);
    }

//    public static OrderSheet createByString(String orderString) {
//        Map<Menu, Integer> orderRepository = OrderParser.parse(orderString);
//        return new OrderSheet(orderRepository);
//    }
}
