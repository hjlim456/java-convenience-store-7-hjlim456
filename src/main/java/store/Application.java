package store;

import java.io.IOException;

public class Application {
    public static void main(String[] args) {
        try {
            StoreController storeController = new StoreController();
            storeController.run();
        } catch (IOException e) {
            System.out.println("[ERROR] 파일을 읽는 도중 오류가 발생했습니다.");
        }
    }
}
