package chrome.com.chat.user;

import chrome.com.chat.jwt.JwtProvider;
import chrome.com.chat.jwt.JwtService;
import chrome.com.chat.jwt.Token;
import chrome.com.chat.jwt.TokenRepository;
import chrome.com.chat.jwt.dto.JwtResponseDto;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponseStatus;
import chrome.com.chat.user.dto.*;
import chrome.com.chat.user.profile.Profile;
import chrome.com.chat.user.profile.ProfileRepository;
import chrome.com.chat.user.profile.ProfileService;
import chrome.com.chat.user.profile.dto.GetS3Res;
import chrome.com.chat.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@EnableTransactionManagement
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UtilService utilService;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final JwtService jwtService;
    private final ProfileRepository profileRepository;
    private final ProfileService profileService;
    private final S3Service s3Service;
    private final RedisTemplate redisTemplate;

    /**
     * 유저 생성 후 DB에 저장(회원 가입) with JWT
     */
    @Transactional
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        if(userRepository.findByEmailCount(postUserReq.getEmail()) >= 1) {
            throw new BaseException(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
        }
        if(postUserReq.getPassword().isEmpty()){
            throw new BaseException(BaseResponseStatus.PASSWORD_CANNOT_BE_NULL);
        }
        if(!postUserReq.getPassword().equals(postUserReq.getPasswordChk())) {
            throw new BaseException(BaseResponseStatus.PASSWORD_MISSMATCH);
        }

        if(postUserReq.getNickName() == null || postUserReq.getNickName().isEmpty()) {
            throw new BaseException(BaseResponseStatus.NICKNAME_CANNOT_BE_NULL);
        }

        String pwd;
        try{
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword()); // 암호화 코드
        }
        catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
        }
        User user = new User();
        user.createUser(postUserReq.getNickName(),postUserReq.getEmail(), pwd, null);
        userRepository.save(user);

        return new PostUserRes(user);
    }

    /**
     * 유저 로그인 with JWT
     */
    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        User user = utilService.findByEmailWithValidation(postLoginReq.getEmail());
        user.setUid(postLoginReq.getUid());

        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(BaseResponseStatus.PASSWORD_DECRYPTION_ERROR);
        }

        if (postLoginReq.getPassword().equals(password)) {
            Token token = tokenRepository.findTokenByUserId(user.getId()).orElse(null);
            if (token == null) {
                JwtResponseDto.TokenInfo tokenInfo = jwtProvider.generateToken(user.getId());
                token = Token.builder()
                        .accessToken(tokenInfo.getAccessToken())
                        .refreshToken(tokenInfo.getRefreshToken())
                        .user(user)
                        .build();
                tokenRepository.save(token);
            }

            return new PostLoginRes(user, token);
        } else {
            throw new BaseException(BaseResponseStatus.PASSWORD_NOT_MATCH);
        }
    }

    /**
     * 로그아웃
     */
    @Transactional
    public String logout(Long userId) throws BaseException {
        try {
            if (userId == 0L) { // 로그아웃 요청은 access token이 만료되더라도 재발급할 필요가 없음.
                User user = tokenRepository.findUserByAccessToken(jwtService.getJwt()).orElse(null);
                if (user != null) {
                    Token token = tokenRepository.findTokenByUserId(user.getId()).orElse(null);
                    tokenRepository.deleteTokenByAccessToken(token.getAccessToken());
                    return "로그아웃 되었습니다.";
                }

                else {
                    throw new BaseException(BaseResponseStatus.INVALID_JWT);
                }
            }

            else { // 토큰이 만료되지 않은 경우
                User logoutUser = utilService.findByUserIdWithValidation(userId);
                Token token = utilService.findTokenByUserIdWithValidation(logoutUser.getId());
                String accessToken = token.getAccessToken();

                //엑세스 토큰 남은 유효시간
                Long expiration = jwtProvider.getExpiration(accessToken);

                //Redis Cache에 저장
                redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

                //리프레쉬 토큰 삭제
                tokenRepository.deleteTokenByUserId(logoutUser.getId());
                return "로그아웃 되었습니다.";
            }
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.FAILED_TO_LOGOUT);
        }
    }

    /**
     * 유저 정보 반환
     */
    public GetUserRes getUserInfo(String uid) throws BaseException {
        User user = utilService.findByUserUidWithValidation(uid);
        String profileUrl = (user.getProfile() != null) ? user.getProfile().getProfileUrl() : null;
        String nickName = user.getNickName();

        return new GetUserRes(uid, profileUrl, nickName);
    }

    /**
     *  유저 닉네임 변경
     */
    @Transactional
    public String modifyUserNickName(Long userId, String nickName) throws BaseException {
        User user = utilService.findByUserIdWithValidation(userId);
        user.setNickName(nickName);
        return "회원정보가 수정되었습니다.";
    }

    /**
     *  유저 비밀번호 변경
     */
    @Transactional
    public String modifyPassword(Long userId, PatchPasswordReq patchPasswordReq) throws BaseException {
        try {
            User user = utilService.findByUserIdWithValidation(userId);
            String password;
            try {
                password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
            } catch (Exception ignored) {
                throw new BaseException(BaseResponseStatus.PASSWORD_DECRYPTION_ERROR);
            }
            // 이전 비밀번호가 일치하지 않는 경우
            if (!patchPasswordReq.getExPassword().equals(password)) {
                throw new BaseException(BaseResponseStatus.EX_PASSWORD_MISSMATCH);
            }
            // 이전 비밀번호와 새 비밀번호가 일치하는 경우
            if(patchPasswordReq.getNewPassword().equals(patchPasswordReq.getExPassword())) {
                throw new BaseException(BaseResponseStatus.CANNOT_UPDATE_PASSWORD);
            }
            // 새 비밀번호와 새 비밀번호 확인이 일치하지 않는 경우
            if(!patchPasswordReq.getNewPassword().equals(patchPasswordReq.getNewPasswordChk())) {
                throw new BaseException(BaseResponseStatus.PASSWORD_MISSMATCH);
            }

            String pwd;
            try{
                pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(patchPasswordReq.getNewPassword()); // 암호화코드
            }
            catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
                throw new BaseException(BaseResponseStatus.PASSWORD_ENCRYPTION_ERROR);
            }
            user.setPassword(pwd);
            return "비밀번호 변경이 완료되었습니다.";
        } catch (BaseException exception) {
            throw new BaseException(exception.getStatus());
        }
    }

    /**
     *  유저 프로필 변경
     */
    @Transactional
    public String modifyProfile(Long userId, MultipartFile multipartFile) throws BaseException {
        try {
            User user = utilService.findByUserIdWithValidation(userId);
            Profile profile = profileRepository.findProfileById(userId).orElse(null);
            if(profile == null) { // 프로필이 미등록된 사용자가 변경을 요청하는 경우
                GetS3Res getS3Res;
                if(multipartFile != null) {
                    getS3Res = s3Service.uploadSingleFile(multipartFile);
                    profileService.saveProfile(getS3Res, user);
                }
            }
            else { // 프로필이 등록된 사용자가 변경을 요청하는 경우
                // 1. 버킷에서 삭제
                profileService.deleteProfile(profile);
                // 2. Profile Repository에서 삭제
                profileService.deleteProfileById(userId);
                if(multipartFile != null) {
                    GetS3Res getS3Res = s3Service.uploadSingleFile(multipartFile);
                    profileService.saveProfile(getS3Res, user);
                }
            }
            return "프로필 수정이 완료되었습니다.";
        } catch (BaseException exception) {
            throw new BaseException(exception.getStatus());
        }
    }

    /**
     * 모든 유저의 닉네임과 프로필 사진 반환
     */
    public List<GetUserRes> getUsers(Long userId) {
        utilService.findByUserIdWithValidation(userId);
        List<User> users = userRepository.findUserByIdWithoutMe(userId);

        List<GetUserRes> getUserResList = users.stream()
                .map(user -> {
                    String profileUrl = (user.getProfile() != null) ? user.getProfile().getProfileUrl() : null;
                    return new GetUserRes(user.getUid(), profileUrl, user.getNickName());
                })
                .sorted(Comparator.comparing(GetUserRes::getNickName))
                .collect(Collectors.toList());

        return getUserResList;
    }

    /**
     *  유저 탈퇴
     */
    @Transactional
    public String deleteUser(Long userId, String agreement) throws BaseException{
        if(!agreement.equals("I agree")) {
            throw new BaseException(BaseResponseStatus.AGREEMENT_MISMATCH);
        }
        User user = utilService.findByUserIdWithValidation(userId);
        tokenRepository.deleteTokenByUserId(userId);
        Profile profile = profileRepository.findProfileById(userId).orElse(null);
        if(profile != null) {
            profileService.deleteProfile(profile);
            profileRepository.deleteProfileById(userId);
        }
        userRepository.deleteUser(userId);
        String result = "요청하신 회원에 대한 삭제가 완료되었습니다.";
        return result;
    }
}
