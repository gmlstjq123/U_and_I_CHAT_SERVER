package chrome.com.chat.user;

import chrome.com.chat.jwt.Token;
import chrome.com.chat.user.profile.Profile;
import chrome.com.chat.utils.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTimeEntity {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 멤버의 식별자

    @Column(nullable = true)
    private String uid;

    @Column(nullable = false)
    private String nickName; // 유저의 닉네임

    @Column(nullable = false)
    private String email; // 이메일로 로그인

    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private String deviceToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile; // 프로필 사진과 일대일 매핑

    public User createUser(String nickName, String email, String password, String uid) {
        this.nickName= nickName;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.deviceToken = null;
        return this;
    }
}
