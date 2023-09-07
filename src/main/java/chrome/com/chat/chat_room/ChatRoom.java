package chrome.com.chat.chat_room;

import chrome.com.chat.user_chat_room.UserChatRoom;
import chrome.com.chat.utils.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoom extends BaseTimeEntity {
    @Id
    private String chatRoomId;

    @Column(nullable = false)
    private String roomName; // 채팅방 이름

    @Column(nullable = false)
    private Integer userCount; // 채팅방 인원 수

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    public void updateUserCount(int userCount){
        this.userCount = userCount;
    }
}
