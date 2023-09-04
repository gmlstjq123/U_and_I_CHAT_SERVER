package chrome.com.chat.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUserReq {
    private String nickName;
    private String email;
    private String password;
    private String passwordChk; // 비밀번호 확인
}
