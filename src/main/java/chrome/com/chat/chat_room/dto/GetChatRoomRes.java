package chrome.com.chat.chat_room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetChatRoomRes {
    private String chatRoomId;
    private String roomName;
    private String userList; // 유저의 이름을 쉼표로 구분
}
