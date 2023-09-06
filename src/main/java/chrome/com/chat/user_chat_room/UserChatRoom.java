package chrome.com.chat.user_chat_room;

import chrome.com.chat.chat_room.ChatRoom;
import chrome.com.chat.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserChatRoom { // 유저와 채팅방의 다대다 관계 매핑을 위한 연결 클래스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userChatRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public void setChatRoom(ChatRoom chatRoom){
        this.chatRoom = chatRoom;
    }

    public void setUser(User user){
        this.user = user;
    }
}

