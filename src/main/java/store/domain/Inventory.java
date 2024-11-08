package store.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import store.Util.FileLoader;
import store.domain.discount.Promotion;

public class Inventory {
    private final static String LINE_SPLIT_SEPARATOR = ",";

    private final List<Product> products = new ArrayList<>();


    public Inventory(String filePath) {
        initializeFromFile(filePath);
    }

    public List<Product> getProducts() {
        return products;
    }
    private void initializeFromFile(String filePath) {
        try {
            FileLoader.loadByFilePath(filePath).stream()
                    .skip(1)
                    .map(Product::create)
                    .forEach(product -> products.add(product));
        } catch (IOException e) {
            System.out.println("[ERROR] 파일을 읽는 도중 오류가 발생했습니다.");
        }
    }

    public List<Product> findByName(String productName) {
        List<Product> matchedProducts = products.stream()
                .filter(product -> product.getName().equals(productName))
                .toList();
        if (matchedProducts.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
        }
        return matchedProducts;
    }

}