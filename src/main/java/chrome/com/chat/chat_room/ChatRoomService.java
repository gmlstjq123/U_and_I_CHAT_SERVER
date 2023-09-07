package chrome.com.chat.chat_room;

import chrome.com.chat.chat_room.dto.GetChatRoomRes;
import chrome.com.chat.jwt.TokenRepository;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponseStatus;
import chrome.com.chat.chat_room.dto.AddUserReq;
import chrome.com.chat.user.User;
import chrome.com.chat.user.dto.GetUserRes;
import chrome.com.chat.user_chat_room.UserChatRoom;
import chrome.com.chat.user_chat_room.UserChatRoomRepository;
import chrome.com.chat.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomRepository userChatRoomRepository;
    private final TokenRepository tokenRepository;
    private final UtilService utilService;

    @Transactional
    public String createChatRoom(Long userId, String roomName) throws BaseException {
        ChatRoom chatRoom = ChatRoom.builder()
                .chatRoomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .userCount(1) // 채팅방 참여 인원수
                .build();
        User user = utilService.findByUserIdWithValidation(userId);
        UserChatRoom userChatRoom = new UserChatRoom();
        userChatRoom.setChatRoom(chatRoom);
        userChatRoom.setUser(user);
        chatRoomRepository.save(chatRoom);
        userChatRoomRepository.save(userChatRoom);
        return chatRoom.getChatRoomId();
    }

    // 채팅방 인원+1
    @Transactional
    public void plusUserCount(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomId).orElse(null);
        chatRoom.updateUserCount(chatRoom.getUserCount() + 1);
        chatRoomRepository.save(chatRoom);
    }

    // 채팅방 인원-1
    @Transactional
    public void minusUserCount(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(roomId).orElse(null);
        chatRoom.updateUserCount(chatRoom.getUserCount() - 1);
        chatRoomRepository.save(chatRoom);
    }
    public String getNickNameList(String chatRoomId) throws BaseException {
        utilService.findChatRoomByChatRoomIdWithValidation(chatRoomId);
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomByRoomId(chatRoomId);
        List<String> nickNameList = userChatRooms.stream()
                .map(userChatRoom -> {
                    String nickName = userChatRoom.getUser().getNickName();
                    return nickName;
                })
                .collect(Collectors.toList());
        String result = String.join(", ", nickNameList);
        return result;
    }

    public List<GetUserRes> getUserListById(String chatRoomId) throws BaseException {
        utilService.findChatRoomByChatRoomIdWithValidation(chatRoomId);
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserChatRoomByRoomId(chatRoomId);
        List<GetUserRes> getUserRes = userChatRooms.stream()
                .map(userChatRoom -> {
                    String uid = userChatRoom.getUser().getUid();
                    String nickName = userChatRoom.getUser().getNickName();
                    String profileUrl = Optional.ofNullable(userChatRoom.getUser().getProfile())
                            .map(profile -> profile.getProfileUrl())
                            .orElse(null);

                    return new GetUserRes(uid, profileUrl, nickName);
                })
                .collect(Collectors.toList());
        return getUserRes;
    }

    public String getUserCount(String chatRoomId) throws BaseException {
        ChatRoom chatRoom = utilService.findChatRoomByChatRoomIdWithValidation(chatRoomId);
        return chatRoom.getUserCount().toString();
    }

    public List<GetChatRoomRes> getChatRoomListById(Long userId) {
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findUserListByUserId(userId);
        List<GetChatRoomRes> getChatRoomRes = userChatRooms.stream()
                .map(userChatRoom -> {
                    return new GetChatRoomRes(userChatRoom.getChatRoom().getChatRoomId(),
                            userChatRoom.getChatRoom().getRoomName(),
                            getNickNameList(userChatRoom.getChatRoom().getChatRoomId()));
                })
                .collect(Collectors.toList());
        return getChatRoomRes;
    }

    // 채팅방 나가기
    @Transactional
    public String exitChatRoom(Long userId, String roomId) throws BaseException {
        utilService.findChatRoomByChatRoomIdWithValidation(roomId);
        userChatRoomRepository.deleteUserChatRoomByUserIdWithRoomId(userId, roomId);
        minusUserCount(roomId);
        ChatRoom chatRoom = utilService.findChatRoomByChatRoomIdWithValidation(roomId);
        if (chatRoom.getUserCount() == 0) { // 채팅방에 아무도 안 남게 되면 Repository에서 삭제
            userChatRoomRepository.deleteUserChatRoomsByRoomId(roomId);
            chatRoomRepository.deleteChatRoomById(roomId);
        }
        // 만약 사진 업로드 기능을 추가한다면 S3에 올라간 파일도 삭제해주어야 함
        User user = utilService.findByUserIdWithValidation(userId);
        String result = user.getNickName() + "님이 " + roomId + "번 채팅방을 나갔습니다.";
        return result;
    }

    @Transactional
    public String addUser(AddUserReq addUserReq) throws BaseException {
        try {
            ChatRoom chatRoom = utilService.findChatRoomByChatRoomIdWithValidation(addUserReq.getRoomId());
            User user = utilService.findByUserUidWithValidation(addUserReq.getUid());
            UserChatRoom userChatRoom = userChatRoomRepository.findUserChatRoomByUserIdWithRoomId(user.getId(), chatRoom.getChatRoomId()).orElse(null);
            if (userChatRoom != null) { // 이미 채팅방에 추가된 유저인 경우
                throw new BaseException(BaseResponseStatus.ALREADY_EXIST_MEMBER);
            }
            userChatRoom = UserChatRoom.builder()
                    .user(user)
                    .chatRoom(chatRoom)
                    .build();

            userChatRoomRepository.save(userChatRoom);
            plusUserCount(addUserReq.getRoomId());
            return user.getNickName();
        } catch (BaseException exception) {
            throw new BaseException(exception.getStatus());
        }
    }
}