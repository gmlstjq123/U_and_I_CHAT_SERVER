package chrome.com.chat.user.dto;

import chrome.com.chat.jwt.Token;
import chrome.com.chat.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostLoginRes {
    private Long userId;
    private String accessToken;
    private String refreshToken;

    public PostLoginRes(User user, Token token) {
        this.userId = user.getId();
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
