package chrome.com.chat.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private BaseResponseStatus status; //BaseResponseStatus 객체에 매핑
}
