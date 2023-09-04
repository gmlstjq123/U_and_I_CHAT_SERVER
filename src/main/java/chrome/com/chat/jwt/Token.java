package chrome.com.chat.jwt;

import chrome.com.chat.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenId; // 토큰의 식별자

    @Column(nullable = true)
    private String accessToken;

    @Column(nullable = true)
    private String refreshToken;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateAccessToken(String accessToken){
        this.accessToken = accessToken;
    }
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void updateUser(User user){
        this.user = user;
    }
}
