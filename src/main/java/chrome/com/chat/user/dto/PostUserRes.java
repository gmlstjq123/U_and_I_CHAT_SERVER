package chrome.com.chat.user.dto;

import chrome.com.chat.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostUserRes {
    private Long userId;
    private String nickName;

    public PostUserRes(User user){
        this.userId = user.getId();
        this.nickName = user.getNickName();
    }
}
