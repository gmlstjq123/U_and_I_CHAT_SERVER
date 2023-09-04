package chrome.com.chat.utils;

import chrome.com.chat.jwt.Token;
import chrome.com.chat.jwt.TokenRepository;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponseStatus;
import chrome.com.chat.user.User;
import chrome.com.chat.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static chrome.com.chat.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public User findByUserIdWithValidation(Long userId) throws BaseException {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new BaseException(NONE_EXIST_USER));
    }

    public User findByUserUidWithValidation(String uid) throws BaseException {
        return userRepository.findUserByUid(uid)
                .orElseThrow(() -> new BaseException(NONE_EXIST_USER));
    }

    public User findByEmailWithValidation(String email) throws BaseException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(POST_USERS_NONE_EXISTS_EMAIL));
    }

    public Token findTokenByUserIdWithValidation(Long userId) throws BaseException {
        return tokenRepository.findTokenByUserId(userId)
                .orElseThrow(() -> new BaseException(INVALID_JWT));
    }
}
