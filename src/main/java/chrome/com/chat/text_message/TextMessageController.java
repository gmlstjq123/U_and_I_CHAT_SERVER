package chrome.com.chat.text_message;

import chrome.com.chat.chat_room.ChatRoomService;
import chrome.com.chat.jwt.JwtService;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponse;
import chrome.com.chat.text_message.dto.PostMessageReq;
import chrome.com.chat.text_message.dto.SendMessageReq;
import chrome.com.chat.utils.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TextMessageController {
    private final ChatRoomService chatRoomService;
    private final UtilService utilService;
    private final JwtService jwtService;

    @PostMapping("/message/send")
    public BaseResponse<String> SendMessage(@RequestBody SendMessageReq sendMessageReq) {
        try {
            Long userId = jwtService.getUserIdx();
            PostMessageReq postMessageReq = new PostMessageReq(sendMessageReq.getRoomId(), sendMessageReq.getMessage());
            return new BaseResponse<>(chatRoomService.sendMessage(userId, postMessageReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
