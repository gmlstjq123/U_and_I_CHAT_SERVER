package chrome.com.chat.jwt.naver;

import chrome.com.chat.jwt.JwtProvider;
import chrome.com.chat.jwt.Token;
import chrome.com.chat.jwt.dto.JwtResponseDto;
import chrome.com.chat.jwt.kakao.dto.PostKakaoLoginRes;
import chrome.com.chat.jwt.naver.dto.GetNaverUserRes;
import chrome.com.chat.jwt.naver.dto.PostNaverLoginRes;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponse;
import chrome.com.chat.user.User;
import chrome.com.chat.user.UserRepository;
import chrome.com.chat.user.UserService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NaverService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    /**
     * 네이버 콜백 메서드
     */
    public PostNaverLoginRes naverCallBack(String accessToken) throws BaseException {
        GetNaverUserRes getNaverUserRes = getUserInfo(accessToken);
        String email = getNaverUserRes.getEmail();
        String nickName = getNaverUserRes.getNickName();
        Optional<User> findUser = userRepository.findByEmail(email);

        JwtResponseDto.TokenInfo tokenInfo;
        if (!findUser.isPresent()) { // 회원가입인 경우
            User naverUser = new User();
            naverUser.createUser(nickName, email, null, null);
            userRepository.save(naverUser);
            tokenInfo = jwtProvider.generateToken(naverUser.getId());
            return new PostNaverLoginRes(naverUser.getId(), naverUser.getEmail(), tokenInfo.getAccessToken(), tokenInfo.getRefreshToken());
        }
        else { // 기존 회원이 로그인하는 경우
            User user = findUser.get();
            tokenInfo = jwtProvider.generateToken(user.getId());
            return new PostNaverLoginRes(user.getId(), user.getEmail(), tokenInfo.getAccessToken(), tokenInfo.getRefreshToken());
        }
    }

    /**
     * 네이버 유저의 정보 가져오기
     */
    public GetNaverUserRes getUserInfo(String accessToken) throws BaseException {
        // HttpHeader 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 객체에 담기
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        // Http 요청을 GET 방식으로 실행하여 멤버 정보를 가져옴
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // 네이버 인증 서버가 반환한 사용자 정보
        String userInfo = responseEntity.getBody();

        // JSON 데이터에서 필요한 정보 추출
        Gson gsonObj = new Gson();
        Map<?, ?> data = gsonObj.fromJson(userInfo, Map.class);
        // 유저의 이메일 정보 가져오기
        String email = (String) ((Map<?, ?>) (data.get("response"))).get("email");
        // 유저의 닉네임 정보 가져오기
        String nickName = (String) ((Map<?, ?>) (data.get("response"))).get("nickname");
        return new GetNaverUserRes(email, nickName);
    }
}
