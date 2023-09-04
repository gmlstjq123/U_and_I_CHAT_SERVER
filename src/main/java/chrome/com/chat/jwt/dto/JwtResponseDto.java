package chrome.com.chat.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class JwtResponseDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TokenInfo {
        private String accessToken;
        private String refreshToken;
    }
}
