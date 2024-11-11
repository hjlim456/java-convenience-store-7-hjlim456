package store.constant;

public enum FileAddress {
    PROMOTIONS_FILE_ADDRESS("src/main/resources/promotions.md"),
    PRODUCTS_FILE_ADDRESS ("src/main/resources/products.md");
    ;
    private final String message;

    FileAddress(String message) {
        this.message = message;
    }

    public static String getMessage(FileAddress fileAddress) {
        return fileAddress.message;
    }
}
