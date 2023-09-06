package chrome.com.chat.text_message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TextMessageRepository extends JpaRepository<TextMessage, Long> {
    @Query("select t from TextMessage t where t.chatRoom.chatRoomId = :roomId")
    List<TextMessage> findMessagesByRoomId(@Param("roomId") String roomId);

    @Modifying
    @Query("delete from TextMessage t where t.chatRoom.chatRoomId = :roomId")
    void deleteMessageByRoomId(@Param("roomId") String roomId);

    @Query("select t from TextMessage t join fetch t.sender s where t.textMessageId =:messageId")
    Optional<TextMessage> findByMessageId(@Param("messageId") Long messageId);
}