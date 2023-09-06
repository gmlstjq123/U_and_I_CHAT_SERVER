package chrome.com.chat.user_chat_room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {
    @Query("select uc from UserChatRoom uc where uc.user.id = :userId and uc.chatRoom.chatRoomId = :chatRoomId")
    Optional<UserChatRoom> findUserChatRoomByUserIdWithRoomId(@Param("userId") Long userId, @Param("chatRoomId") String chatRoomId);

    @Query("select uc from UserChatRoom uc where uc.chatRoom.chatRoomId = :chatRoomId")
    List<UserChatRoom> findUserChatRoomByRoomId(@Param("chatRoomId") String chatRoomId);

    @Query("select uc from UserChatRoom uc where uc.user.id = :userId")
    List<UserChatRoom> findUserListByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("delete from UserChatRoom uc where uc.chatRoom.chatRoomId = :chatRoomId")
    void deleteUserChatRoomsByRoomId(@Param("chatRoomId") String chatRoomId);

    @Modifying
    @Query("delete from UserChatRoom uc where uc.user.id = :userId and uc.chatRoom.chatRoomId = :chatRoomId")
    void deleteUserChatRoomByUserIdWithRoomId(@Param("userId") Long userId, @Param("chatRoomId") String chatRoomId);
}