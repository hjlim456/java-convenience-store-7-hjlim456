package store.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import store.Util.FileLoader;

public class Inventory {
    private final static String LINE_SPLIT_SEPARATOR = ",";

    private final List<Product> products = new ArrayList<>();

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public Inventory(String filePath) {
        initializeFromFile(filePath);
    }

    private void initializeFromFile(String filePath) {
        try {
            FileLoader.loadByFilePath(filePath).stream()
                    .skip(1)
                    .map(Product::createProduct)
                    .forEach(products::add);
        } catch (IOException e) {
            System.out.println("[ERROR] 파일을 읽는 도중 오류가 발생했습니다.");
        }
    }
    public Product getProductByName(String productName) {
        return products.stream()
                .filter(product -> product.getName().equals(productName))
                .findFirst()
                .orElseGet(() -> {
                    throw  new IllegalArgumentException("[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요.");
                });
    }
    public void purchaseProduct(String productName, int count) {
        Product product = getProductByName(productName);
        product.decreaseQuantity(count);
    }
}
