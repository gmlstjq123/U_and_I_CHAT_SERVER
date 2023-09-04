package chrome.com.chat.response;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    NONE_EXIST_USER(false, 2006, "존재하지 않는 사용자입니다."),
    NONE_EXIST_NICKNAME(false, 2007, "존재하지 않는 닉네임입니다."),
    // users
    PASSWORD_CANNOT_BE_NULL(false, 2011, "비밀번호를 입력해주세요."),
    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_NONE_EXISTS_EMAIL(false,2018,"등록되지 않은 이메일입니다."),
    LOG_OUT_USER(false,2019,"이미 로그아웃된 유저입니다."),
    NICKNAME_CANNOT_BE_NULL(false, 2020, "닉네임을 입력해주세요"),
    ALREADY_LOGIN(false, 2021, "이미 로그인된 유저입니다."),
    AGREEMENT_MISMATCH(false, 2023, "동의 문구를 잘못 입력하셨습니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),
    PASSWORD_NOT_MATCH(false,3014,"비밀번호가 틀렸습니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),
    PASSWORD_MISSMATCH(false, 4013, "비밀번호가 일치하지 않습니다."),
    EX_PASSWORD_MISSMATCH(false, 4014, "현재 비밀번호가 잘못되었습니다."),
    CANNOT_UPDATE_PASSWORD(false, 4016, "같은 비밀번호로 수정할 수 없습니다."),

    /**
     *   8000 : 토큰 관련 오류
     */
    EXPIRED_USER_JWT(false,8000,"만료된 JWT입니다."),
    REISSUE_TOKEN(false, 8001, "토큰이 만료되었습니다. 다시 로그인해주세요."),
    FAILED_TO_UPDATE(false, 8002, "토큰을 만료시키는 작업에 실패하였습니다."),
    FAILED_TO_REFRESH(false, 8003, "토큰 재발급에 실패하였습니다."),

    /**
     *   9000 : 채팅 관련 오류
     */
    CANNOT_CREATE_ROOM(false, 9000, "혼자만의 채팅방은 만들 수 없습니다."),
    ALREADY_EXIST_MEMBER(false, 9001, "이미 추가된 유저입니다."),
    FAILED_TO_ENTER(false, 9002, "채팅방 입장에 실패하였습니다."),
    NONE_EXIST_ROOM(false, 9003, "요청하신 채팅방은 존재하지 않습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
