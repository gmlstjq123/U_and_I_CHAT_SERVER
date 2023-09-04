package chrome.com.chat.user;

import chrome.com.chat.jwt.JwtService;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponse;
import chrome.com.chat.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * 회원 가입
     */
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        try {
            return new BaseResponse<>(userService.createUser(postUserReq));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인
     */
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> loginUser(@RequestBody PostLoginReq postLoginReq) {
        try {
            return new BaseResponse<>(userService.login(postLoginReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 정보 반환
     */
    @GetMapping("")
    public BaseResponse<GetUserRes> getUserInfo(@RequestParam String uid) {
        try {
            return new BaseResponse<>(userService.getUserInfo(uid));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 닉네임 변경
     */
    @PatchMapping("/nickname")
    public BaseResponse<String> modifyUserName(@RequestParam String nickName) {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.modifyUserNickName(userId, nickName));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 비밀번호 변경
     */
    @PatchMapping("/password")
    public BaseResponse<String> modifyPassword(@RequestBody PatchPasswordReq patchPasswordReq) {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.modifyPassword(userId, patchPasswordReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 프로필 사진 변경
     */
    @PatchMapping("/profile")
    public BaseResponse<String> modifyProfile(@RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.modifyProfile(userId, multipartFile));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
