package chrome.com.chat.chat_room;

import chrome.com.chat.chat_room.dto.AddUserReq;
import chrome.com.chat.chat_room.dto.GetChatRoomRes;
import chrome.com.chat.jwt.JwtService;
import chrome.com.chat.response.BaseException;
import chrome.com.chat.response.BaseResponse;
import chrome.com.chat.user.dto.GetUserRes;
import chrome.com.chat.utils.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;
    private final JwtService jwtService;
    private final UtilService utilService;

    // 채팅방 생성
    @PostMapping("/room")
    public BaseResponse<String> CreateChatRoom(@RequestParam String roomName) {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(chatRoomService.createChatRoom(userId, roomName));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 내가 속한 채팅방 리스트 반환
    @GetMapping("/room")
    public BaseResponse<List<GetChatRoomRes>> getChatRoomList() {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(chatRoomService.getChatRoomListById(userId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 채팅에 참여한 유저 리스트 반환, 채팅방 안에서 호출
    @GetMapping("/room/{roomId}")
    public BaseResponse<List<GetUserRes>> getUserList(@PathVariable String roomId) {
        try {
            return new BaseResponse<>(chatRoomService.getUserListById(roomId));

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 채팅에 참여한 유저의 UID 리스트 반환
    @GetMapping("/uidList/{roomId}")
    public BaseResponse<List<String>> getUserUidList(@PathVariable String roomId) {
        try {
            return new BaseResponse<>(chatRoomService.getUserUidListById(roomId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 친구 초대
    @PostMapping("/room/add")
    public BaseResponse<String> AddUser(@RequestBody AddUserReq addUserReq) {
        try {
            return new BaseResponse<>(chatRoomService.addUser(addUserReq));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 채팅방에 참여한 인원 수 반환
    @GetMapping("/userCount/{roomId}")
    public BaseResponse<String> getUserCount(@PathVariable String roomId) {
        try {
            return new BaseResponse<>(chatRoomService.getUserCount(roomId));

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 채팅방에 참여한 유저의 닉네임 List를 String으로 반환
    @GetMapping("/userList/{roomId}")
    public BaseResponse<String> getUserStrList(@PathVariable String roomId) {
        try {
            return new BaseResponse<>(chatRoomService.getNickNameList(roomId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 채팅에 참여한 유저 중 본인을 제외한 유저의 디바이스 토큰 목록 반환
    @GetMapping("/tokenList/{roomId}")
    public BaseResponse<List<String>> getTokenList(@PathVariable String roomId) {
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(chatRoomService.getTokenList(userId, roomId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 채팅방 나가기
    @DeleteMapping("/room/{roomId}")
    public BaseResponse<String> exitChatRoom(@PathVariable String roomId){
        try {
            Long userId = jwtService.getUserIdx();
            return new BaseResponse<>(chatRoomService.exitChatRoom(userId, roomId));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 이미지 S3 업로드
    @PostMapping("/image")
    public BaseResponse<String> uploadImage(@RequestPart(value = "image", required = false) MultipartFile multipartFile) {
        try {
            return new BaseResponse<>(chatRoomService.uploadImage(multipartFile));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
