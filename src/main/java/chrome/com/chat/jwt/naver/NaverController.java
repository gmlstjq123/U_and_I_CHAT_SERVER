package chrome.com.chat.jwt.naver;

import chrome.com.chat.jwt.kakao.dto.PostKakaoLoginRes;
import chrome.com.chat.jwt.naver.dto.PostNaverLoginRes;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NaverController {

    private final NaverService naverService;

    /**
     * 네이버 소셜로그인
     */
    @ResponseBody
    @PostMapping("/oauth/naver")
    public BaseResponse<PostNaverLoginRes> naverCallback(@RequestParam("token") String accessToken) {
        try {
            return new BaseResponse<>(naverService.naverCallBack(accessToken));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}