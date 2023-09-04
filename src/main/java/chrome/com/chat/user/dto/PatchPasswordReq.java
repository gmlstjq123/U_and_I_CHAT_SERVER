package chrome.com.chat.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchPasswordReq {
    private String exPassword; // 이전 비밀번호
    private String newPassword; // 새 비밀번호
    private String newPasswordChk; // 새 비밀번호 확인
}
