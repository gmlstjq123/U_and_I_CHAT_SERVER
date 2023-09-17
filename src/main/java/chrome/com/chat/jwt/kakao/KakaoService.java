package chrome.com.chat.jwt.kakao;

import chrome.com.chat.jwt.JwtProvider;
import chrome.com.chat.jwt.Token;
import chrome.com.chat.jwt.dto.JwtResponseDto;
import chrome.com.chat.jwt.kakao.dto.GetKakaoUserRes;
import chrome.com.chat.jwt.kakao.dto.PostKakaoLoginRes;
import chrome.com.chat.jwt.kakao.dto.PostKakaoUserReq;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponse;
import chrome.com.chat.response.BaseResponseStatus;
import chrome.com.chat.user.User;
import chrome.com.chat.user.UserRepository;
import chrome.com.chat.user.UserService;
import chrome.com.chat.user.dto.PostLoginRes;
import chrome.com.chat.utils.UtilService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final UtilService utilService;

    /**
     * 카카오 콜백 메서드
     */
    public PostKakaoLoginRes kakaoCallBack(String accessToken) throws BaseException {
        GetKakaoUserRes getKakaoUserRes = getUserInfo(accessToken);
        String email = getKakaoUserRes.getEmail();
        String nickName = getKakaoUserRes.getNickName();
        Optional<User> findUser = userRepository.findByEmail(email);
        JwtResponseDto.TokenInfo tokenInfo;
        if (!findUser.isPresent()) { // 회원가입인 경우
            User kakaoUser = new User();
            kakaoUser.createUser(nickName, email, null, null);
            userRepository.save(kakaoUser);
            tokenInfo = jwtProvider.generateToken(kakaoUser.getId());
            return new PostKakaoLoginRes(kakaoUser.getId(), kakaoUser.getEmail(), tokenInfo.getAccessToken(), tokenInfo.getRefreshToken());
        }
        else { // 기존 회원이 로그인하는 경우
            User user = findUser.get();
            tokenInfo = jwtProvider.generateToken(user.getId());
            return new PostKakaoLoginRes(user.getId(), user.getEmail(), tokenInfo.getAccessToken(), tokenInfo.getRefreshToken());
        }

    }

    /**
     * 카카오 유저의 정보 가져오기
     */
    public GetKakaoUserRes getUserInfo(String accessToken) throws BaseException{
        // HttpHeader 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        httpHeaders.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 객체에 담기(body 정보는 생략 가능)
        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        // RestTemplate를 이용하여 HTTP 요청 처리
        RestTemplate restTemplate = new RestTemplate();

        // Http 요청을 GET 방식으로 실행하여 멤버 정보를 가져옴
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        // 카카오 인증 서버가 반환한 사용자 정보
        String userInfo = responseEntity.getBody();

        // JSON 데이터에서 필요한 정보 추출
        Gson gsonObj = new Gson();
        Map<?, ?> data = gsonObj.fromJson(userInfo, Map.class);

        // 이메일 동의 여부 확인
        boolean emailAgreement = (boolean) ((Map<?, ?>) (data.get("kakao_account"))).get("email_needs_agreement");
        String email;
        if (emailAgreement) { // 사용자가 이메일 동의를 하지 않은 경우
            email = ""; // 대체값 설정
        } else { // 사용자가 이메일 제공에 동의한 경우
            // 이메일 정보 가져오기
            email = (String) ((Map<?, ?>) (data.get("kakao_account"))).get("email");
        }
        if(userRepository.findByEmailCount(email) >= 1 && email != "") {
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }
        // 닉네임 동의 여부 확인
        boolean nickNameAgreement = (boolean) ((Map<?, ?>) (data.get("kakao_account"))).get("profile_nickname_needs_agreement");
        String nickName;
        if (nickNameAgreement) { // 사용자가 닉네임 동의를 하지 않은 경우
            nickName = ""; // 대체값 설정
        } else { // 사용자가 닉네임 제공에 동의한 경우
            // 닉네임 정보 가져오기
            nickName = (String) ((Map<?, ?>) ((Map<?, ?>) data.get("properties"))).get("nickname");
        }
        return new GetKakaoUserRes(email, nickName);
    }

    /**
     * 카카오 소셜로그인 유저의 uid와 device token 값을 set
     */
    public String saveUidAndToken(Long userId, PostKakaoUserReq postKakaoUserReq) throws BaseException{
        User user = utilService.findByUserIdWithValidation(userId);
        user.setUid(postKakaoUserReq.getUid());
        user.setDeviceToken(postKakaoUserReq.getDeviceToken());
        userRepository.save(user);

        return "UID와 디바이스 토큰이 저장되었습니다.";
    }
}

