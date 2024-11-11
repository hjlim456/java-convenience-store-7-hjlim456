package store.view;

import static store.constant.ExceptionMessage.INVALID_ORDER;
import static store.constant.ViewMessage.INPUT_ORDER;

import camp.nextstep.edu.missionutils.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import store.constant.ExceptionMessage;
import store.constant.ViewMessage;

public class InputView {
    private static final String YES_OR_NO_FORMAT = "^[yYnN]$";
    private static final Pattern YES_OR_NO_PATTERN = Pattern.compile(YES_OR_NO_FORMAT);
    public static String readString() {
        System.out.println(ViewMessage.getMessage(INPUT_ORDER));
        return Console.readLine().trim();
    }

    public static String readYesOrNo() {
            String answer = Console.readLine().trim();
            Matcher matcher = YES_OR_NO_PATTERN.matcher(answer);

            if (!matcher.matches()) {
                throw new IllegalArgumentException("잘못된 입력입니다. 다시 입력해 주세요.");
            }
            return answer;
    }
}
