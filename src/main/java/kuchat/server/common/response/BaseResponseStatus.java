package kuchat.server.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum BaseResponseStatus {
    // 1000 번대 : 요청 성공
    SUCCESS(1000, HttpStatus.OK, "요청에 성공하였습니다."),
    WEBSOCKET_CONNECTION_SUCCESS(1001, HttpStatus.OK, "웹소켓 연결 성공"),

    // 2000 번대 : jwt 관련 상태 코드
    MALFORMED_TOKEN(2000, HttpStatus.UNAUTHORIZED, "토큰이 올바르게 구성되지 않았습니다."),
    NOT_FOUND_TOKEN(2001, HttpStatus.NOT_FOUND, "토큰이 존재하지 않습니다. 다시 로그인 해주세요."),
    UNSUPPORTED_TOKEN(2002, HttpStatus.BAD_REQUEST, "지원하지 않는 토큰 형식입니다."),
    INVALID_TOKEN(2003, HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다. 다시 로그인 해주세요."),
    EXPIRED_TOKEN(2004, HttpStatus.BAD_REQUEST, "유효기간이 만료된 토큰입니다. 다시 로그인 해주세요."),
    INVALID_SIGNATURE(2005, HttpStatus.BAD_REQUEST, "JWT 토큰의 서명이 유효하지 않습니다."),
    ACCESS_DENIED(2006, HttpStatus.UNAUTHORIZED, "요청을 처리할 권한이 없습니다."),

    //- 3000 번대 : oauth 관련 상태 코드
    NOT_FOUND_PLATFORM(3000, HttpStatus.NOT_FOUND, "존재하지 않는 플랫폼입니다."),
    OAUTH2_FAIL(3001, HttpStatus.INTERNAL_SERVER_ERROR, "소셜로그인이 제대로 처리되지 않았습니다. 다시 시도해주세요."),

    //- 4000 번대 : 회원가입/멤버 관련 상태 코드
    NOT_FOUND_LANGUAGE(4000, HttpStatus.NOT_FOUND, "존재하지 않는 언어입니다."),
    NOT_FOUND_MEMBER(4001, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    INFO_BAD_REQUEST(4002, HttpStatus.BAD_REQUEST, "입력된 정보가 형식에 맞지 않습니다. 다시 입력해주세요."),
    DUPLICATED_PLUSID(4003, HttpStatus.BAD_REQUEST, "plus id가 중복됩니다. 다른 문자열로 시도해주세요."),
    NOT_FOUND_ROLE(4004, HttpStatus.BAD_REQUEST, "사용자 권한 정보가 유효하지 않습니다. 다시 시도해주세요."),
    DUPLICATED_STUDENT_ID(4005, HttpStatus.BAD_REQUEST, "이미 회원가입 처리된 학번입니다. 다시 시도해 주세요."),

    //- 5000번대 : 채팅방(chatroom) 관련 코드
    NOT_FOUND_CHATROOM(5000, HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),
    EXIT_CHATROOM_FAIL(5001, HttpStatus.INTERNAL_SERVER_ERROR, "채팅방 나가기에 실패했습니다."),
    MALFORMED_CHATROOM_ID(5002, HttpStatus.BAD_REQUEST, "uri의 채팅방 id가 올바르지 않습니다."),
    DUPLICATED_JOIN(5003, HttpStatus.BAD_REQUEST, "이미 참여하고 있는 회원은 초대할 수 없습니다."),
    EMPTY_CHATROOM(5004, HttpStatus.BAD_REQUEST, "채팅방 참여 인원이 없어 채팅방을 만들 수 없습니다."),
    DUPLICATE_CHATROOM_NAME(5005, HttpStatus.BAD_REQUEST, "이미 존재하는 채팅방 이름입니다. 다른 이름으로 시도해주세요."),
    STOMP_ACCESSOR_NULL(5006, HttpStatus.BAD_REQUEST, "받은 요청의 stomp header accessor가 존재하지 않습니다.(null) 다시 시도해주세요."),
    CHATROOM_BAD_REQUEST(5007, HttpStatus.BAD_REQUEST, "채팅방 생성/수정 요청이 올바르지 않습니다. 다시 시도해 주세요."),

    //- 6000번대 : 메시지(message)/소켓 관련 코드
    WEBSOCKET_CONNECTION_FAIL(6000, HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 서버 접속에 실패했습니다."),
    WEBSOCKET_CLOSE_FAIL(6001, HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 서버와의 연결 종료에 실패했습니다."),
    MESSAGE_FORMAT_ERROR(6002, HttpStatus.BAD_REQUEST, "클라이언트에서 요청한 메시지 json 객체의 형식이 잘못됐습니다."),
    CONVERT_TO_JSON_FAIL(6003, HttpStatus.INTERNAL_SERVER_ERROR, "메시지 객체를 json 형태로 바꾸는데 실패했습니다"),
    CONVERT_TO_OBJECT_FAIL(6004, HttpStatus.INTERNAL_SERVER_ERROR, "json을 메세지 객체 형태로 바꾸는데 실패했습니다"),
    MESSAGE_SEND_FAIL(6005, HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송에 실패했습니다."),
    NOT_FOUND_MESSAGE(6006, HttpStatus.NOT_FOUND, "존재하지 않는 메세지입니다."),
    MESSAGE_LENGTH_MISMATCH(6007, HttpStatus.BAD_REQUEST, "메세지의 payload 길이가 일치하지 않습니다."),
    NOT_FOUND_SESSION(6008, HttpStatus.NOT_FOUND, "사용자의 웹소켓 세션이 존재하지 않습니다. 다시 연결을 시도해주세요."),

    //- 7000번대 : 친구 관련 코드
//    FRIEND_APPLY_SUCCESS(000, HttpStatus.OK, "친구 신청 성공"),
//    FRIEND_ACCEPT_SUCCESS(6001, HttpStatus.CREATED, "친구 신청 수락 성공"),
//    FRIEND_APPLY_REFUSE_SUCCESS(6002, HttpStatus.OK, "친구 신청 거절 성공"),
//    FRIEND_APPLY_LOOKUP_SUCCESS(6003, HttpStatus.OK, "친구 신청 목록 조회 성공"),
//    FRIEND_DELETE_SUCCESS(6004, HttpStatus.OK, "친구 삭제 성공"),
//    BLOCK_MEMBER_SUCCESS(6005, HttpStatus.OK, "사용자 차단 성공"),
//    RELEASE_BLOCK_SUCCESS(6006, HttpStatus.OK, "차단 해제 성공"),
//    BLOCK_LOOKUP_SUCCESS(6007, HttpStatus.OK, "차단 목록 조회 성공"),

    ALREADY_APPLY(7000, HttpStatus.BAD_REQUEST, "둘 사이에 보낸 요청이 존재합니다."),
    NOT_FOUND_FRIEND(7001, HttpStatus.NOT_FOUND, "친구가 아닌 사용자입니다."),
    NOT_FOUND_PLUSID(7002, HttpStatus.NOT_FOUND, "해당 plus id를 사용하는 사용자가 존재하지 않습니다."),
    NOT_FOUND_BLOCK(7003, HttpStatus.NOT_FOUND, "해당 사용자들 사이에 차단 관계가 존재하지 않습니다."),
    BLOCKED_MEMBER(7004, HttpStatus.BAD_REQUEST, "차단하거나 차단 당한 상대의 프로필을 조회할 수 없습니다."),
    ALREADY_FRIEND(7005, HttpStatus.BAD_REQUEST, "이미 친구 관계인 사이에서 친구 관계를 중복하여 맺을 수 없습니다"),

    //- 8000번대 : 알림 관련 코드


    //- 9000번대 : DB 관련 코드
    DB_SUCCESS(8000, HttpStatus.ACCEPTED,"DB에 성공적으로 반영되었습니다."),
    DB_SAVE_FAIL(8001, HttpStatus.INTERNAL_SERVER_ERROR, "DB 저장에 실패했습니다."),
    REDIS_FIND_FAIL(8002, HttpStatus.INTERNAL_SERVER_ERROR, "레디스에서 정보를 조회하는데 실패했습니다.");

    private int code;
    private HttpStatus httpStatus;
    @Setter
    private String message;
}
