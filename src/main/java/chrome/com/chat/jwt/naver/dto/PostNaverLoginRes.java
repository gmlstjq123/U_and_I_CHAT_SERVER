package chrome.com.chat.jwt.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PostNaverLoginRes {
    private Long userId;
    private String email;
    private String accessToken;
    private String refreshToken;
}
