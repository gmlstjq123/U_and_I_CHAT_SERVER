package chrome.com.chat.text_message;

import chrome.com.chat.chat_room.ChatRoom;
import chrome.com.chat.user.User;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@DynamicInsert
@Getter
@Setter
public class TextMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long textMessageId;

    @Column(nullable = false)
    private String content; // 메시지 내용

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isRead; // 메시지 읽음 여부

    @Column(nullable = false)
    private String sendDate;

    @Column(nullable = false)
    private String sendTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
}