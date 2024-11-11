package store.constant;

public enum ExceptionMessage {
    MESSAGE_PREFIX("[ERROR] "),
    INVALID_ORDER("올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요.")
    ;

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public static String getMessage(ExceptionMessage exceptionMessage) {
        return exceptionMessage.message;
    }
}
