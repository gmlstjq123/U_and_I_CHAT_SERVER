package chrome.com.chat.utils;

import chrome.com.chat.chat_room.ChatRoom;
import chrome.com.chat.chat_room.ChatRoomRepository;
import chrome.com.chat.jwt.Token;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponseStatus;
import chrome.com.chat.user.User;
import chrome.com.chat.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

import static chrome.com.chat.response.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

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

    public ChatRoom findChatRoomByChatRoomIdWithValidation(String chatRoomId) throws BaseException {
        return chatRoomRepository.findChatRoomById(chatRoomId)
                .orElseThrow(() -> new BaseException(NONE_EXIST_ROOM));
    }

    public static String formatTime(LocalTime time) {
        int hour = time.getHour();
        int min = time.getMinute();
        String meridiem = (hour >= 12) ? "오후" : "오전";
        if (hour >= 12) {
            hour -= 12;
        }
        return meridiem + " " + hour + ":" + String.format("%02d", min);
    }
}
