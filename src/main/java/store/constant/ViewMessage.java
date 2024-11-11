package store.constant;

public enum ViewMessage {
    ASK_FOR_ANOTHER_PURCHASE_MESAAGE("감사합니다. 구매하고 싶은 다른 상품이 있나요? (Y/N)"),
    INPUT_ORDER("\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])"),
    INPUT_ASK_FOR_ADD_FREE_PRODUCT("\n현재 %s은(는) 1개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)%n")
    ;
    private final String message;

    ViewMessage(String message) {
        this.message = message;
    }

    public static String getMessage(ViewMessage viewMessage) {
        return viewMessage.message;
    }
}
