package chrome.com.chat.jwt.kakao;

import chrome.com.chat.jwt.JwtService;
import chrome.com.chat.jwt.kakao.dto.PostKakaoUserReq;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponse;
import chrome.com.chat.user.dto.PostDeviceTokenReq;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;
    private final JwtService jwtService;

    /**
     * 카카오 소셜로그인
     */
    @ResponseBody
    @PostMapping("/oauth/kakao")
    public BaseResponse<?> kakaoCallback(@RequestParam("token") String accessToken) {
        try {
            return kakaoService.kakaoCallBack(accessToken);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 카카오 소셜로그인 유저의 uid와 device token 값을 set
     */
    @PostMapping("/oauth/device-token")
    public BaseResponse<String> saveUidAndToken(@RequestBody PostKakaoUserReq postKakaoUserReq) {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(kakaoService.saveUidAndToken(userId, postKakaoUserReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
