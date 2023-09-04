package chrome.com.chat.user.profile;

import chrome.com.chat.user.User;
import chrome.com.chat.utils.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Profile extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;
    private String profileUrl; // 프로필 사진 URL
    private String profileFileName; // 프로필 사진명

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user){
        this.user = user;
    }
}