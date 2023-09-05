package chrome.com.chat.user;

import chrome.com.chat.jwt.JwtService;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponse;
import chrome.com.chat.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
     * 로그아웃
     */
    @PostMapping("/log-out") // Redis가 켜져있어야 동작한다.
    public BaseResponse<String> logoutUser() {
        try {
            Long userId = jwtService.getLogoutUserIdx(); // 토큰 만료 상황에서 로그아웃을 시도하면 0L을 반환
            return new BaseResponse<>(userService.logout(userId));
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

    /**
     * 모든 유저의 닉네임과 프로필 사진 반환
     */
    @GetMapping("list-up")
    public BaseResponse<List<GetUserRes>> getUsers() {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.getUsers(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저 탈퇴
     */
    @DeleteMapping("")
    public BaseResponse<String> deleteUser(@RequestParam String agreement){
        // 비밀번호를 입력받아 회원 삭제를 처리하는 로직의 경우, 소셜 로그인 유저에 적용하기 어려움.
        // "계정 삭제에 동의합니다"라는 문구를 입력받는 것(띄어쓰기까지 정확히 일치)으로 회원 삭제를 처리하기로 함.
        try{
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(userService.deleteUser(userId, agreement));
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
