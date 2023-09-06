package chrome.com.chat.text_message.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TextMessageDto {
    private String roomId; // 방 번호
    private Long senderId; // 채팅을 보낸 사람
    private String message; // 메시지
    private String sendDate; // 채팅 발송 날짜
    private String sendTime; // 채팅 발송 시간
}
