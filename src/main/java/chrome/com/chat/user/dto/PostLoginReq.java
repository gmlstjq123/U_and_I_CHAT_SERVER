package chrome.com.chat.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostLoginReq {
    private String uid;
    private String email;
    private String password;
}
