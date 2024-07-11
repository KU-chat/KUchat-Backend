package kuchat.server.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum BaseResponse {
    //- 1000 번대 : jwt 관련 상태 코드
    TOKEN_PROVIDE_SUCCESS(1000, HttpStatus.OK, "jwt 토큰 발급 성공"),
    MALFORMED_TOKEN(1001, HttpStatus.UNAUTHORIZED, "토큰이 올바르게 구성되지 않았습니다."),

    //- 2000 번대 : oauth 관련 상태 코드
    OAUTH_SUCCESS(2000, HttpStatus.OK, "oauth 인증 성공"),
    NOT_FOUND_PLATFORM(2001, HttpStatus.NOT_FOUND, "존재하지 않는 플랫폼입니다."),
    OAUTH2_FAIL(2002, HttpStatus.INTERNAL_SERVER_ERROR, "소셜로그인이 제대로 처리되지 않았습니다. 다시 시도해주세요."),

    //- 3000 번대 : 회원가입/멤버 관련 상태 코드
    SIGNUP_SUCCESS(3000, HttpStatus.CREATED, "회원가입 성공"),
    NOT_FOUND_LANGUAGE(3001, HttpStatus.NOT_FOUND, "존재하지 않는 언어입니다."),
    NOT_FOUND_MEMBER(3002, HttpStatus.NOT_FOUND, "존재하지 않는 회웝입니다."),
    EMAIL_BAD_REQUEST(3003, HttpStatus.BAD_REQUEST, "건국대학교 이메일이 아닙니다."),

    //- 4000번대 : 채팅방(chatroom) 관련 코드
    CHATROOM_SUCCESS(4000, HttpStatus.ACCEPTED, "채팅방 생성/수정 성공"),
    NOT_FOUND_CHATROOM(4001, HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),
    EXIT_CHATROOM_FAIL(4002, HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 나가기에 실패했습니다."),
    MALFORMED_CHATROOM_ID(4003, HttpStatus.BAD_REQUEST, "uri의 채팅방 id가 올바르지 않습니다."),
    DUPLICATED_JOIN(4004, HttpStatus.BAD_REQUEST, "이미 참여하고 있는 회원은 초대할 수 없습니다."),
    EMPTY_CHATROOM(4005, HttpStatus.BAD_REQUEST, "채팅방 참여 인원이 없어 채팅방을 만들 수 없습니다."),

    //- 5000번대 : 메시지(message)/소켓 관련 코드
    MESSAGE_SEND_SUCCESS(5000, HttpStatus.OK, "메세지 전송 성공"),
    WEBSOCKET_CONNECTION_FAIL(5001, HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 서버 접속에 실패했습니다."),
    WEBSOCKET_CLOSE_FAIL(5001, HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 서버와의 연결 종료에 실패했습니다."),
    CONVERT_TO_JSON_FAIL(5002, HttpStatus.INTERNAL_SERVER_ERROR, "메시지 객체를 json 형태로 바꾸는데 실패했습니다"),
    CONVERT_TO_OBJECT_FAIL(5003, HttpStatus.INTERNAL_SERVER_ERROR, "json을 메세지 객체 형태로 바꾸는데 실패했습니다"),
    MESSAGE_SEND_FAIL(5004, HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송해 실패했습니다."),
    NOT_FOUND_MESSAGE(5005, HttpStatus.NOT_FOUND, "존재하지 않는 메세지입니다."),
    MESSAGE_LENGTH_UNMATCH(5006, HttpStatus.BAD_REQUEST, "메세지의 payload 길이가 일치하지 않습니다."),
    NOT_FOUND_SESSION(5007, HttpStatus.NOT_FOUND, "사용자의 웹소켓 세션이 존재하지 않습니다. 다시 연결을 시도해주세요."),
    WEBSOCKET_DISCONNECT(5008, HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 서버와의 연결이 끊겼습니다. 다시 연결을 시도해주세요."),

    //- 6000번대 : 친구 관련 코드

    //- 7000번대 : 알림 관련 코드


    //- 8000번대 : DB 관련 코드
    DB_SUCCESS(8000, HttpStatus.ACCEPTED,"DB에 성공적으로 반영되었습니다."),
    DB_SAVE_FAIL(8001, HttpStatus.INTERNAL_SERVER_ERROR, "DB 저장에 실패했습니다."),
    REDIS_FIND_FAIL(8002, HttpStatus.INTERNAL_SERVER_ERROR, "레디스에서 정보를 조회하는데 실패했습니다.");

    private int code;
    private HttpStatus httpStatus;
    private String message;
}
