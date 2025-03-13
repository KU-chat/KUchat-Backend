package kuchat.server.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum BaseResponseStatus {
    // 1000 번대 : global 요청 성공/실패
    SUCCESS(1000, HttpStatus.OK, "요청에 성공하였습니다."),
    WEBSOCKET_CONNECTION_SUCCESS(1001, HttpStatus.OK, "웹소켓 연결 성공"),

    PARAMETER_NOT_FOUND(1010, HttpStatus.BAD_REQUEST, "요청 url의 쿼리 파라미터가 누락됐습니다."),
    PATH_VARIABLE_NOT_FOUND(1011, HttpStatus.BAD_REQUEST, "요청 url의 path variable이 누락됐습니다."),
    API_NOT_FOUND(1012, HttpStatus.NOT_FOUND, "잘못된 요청입니다. 요청 URL을 확인해주세요."),

    // 2000 번대 : jwt 관련 상태 코드
    MALFORMED_TOKEN(2000, HttpStatus.UNAUTHORIZED, "토큰이 올바르게 구성되지 않았습니다."),
    NOT_FOUND_TOKEN(2001, HttpStatus.NOT_FOUND, "토큰이 존재하지 않습니다. 다시 로그인 해주세요."),
    TOKEN_TYPE_MISMATCH(2002, HttpStatus.BAD_REQUEST, "다른 용도의 토큰입니다."),
    INVALID_TOKEN(2003, HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다. 다시 로그인 해주세요."),
    EXPIRED_TOKEN(2004, HttpStatus.BAD_REQUEST, "유효기간이 만료된 토큰입니다. 다시 로그인 해주세요."),
    ACCESS_DENIED(2006, HttpStatus.UNAUTHORIZED, "요청을 처리할 권한이 없습니다."),
    REFRESH_TOKEN_MISMATCH(2007, HttpStatus.BAD_REQUEST, "리프레시 토큰이 일치하지 않습니다. 다시 로그인 해주세요."),


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
    NOT_SIGNUP_MEMBER(4006, HttpStatus.UNAUTHORIZED, "아직 회원가입 하지 않은 회원입니다. 회원가입 페이지로 이동해 주세요."),
    NOT_FOUND_STATUS(4007, HttpStatus.NOT_FOUND, "사용자 계정 상태가 유효하지 않습니다. 다시 시도해주세요."),
    DATE_BAD_REQUEST(4008, HttpStatus.BAD_REQUEST, "날짜 형식이 올바르지 않습니다. YYYY-MM-DD 형식에 맞게 다시 입력해주세요."),
    IMAGE_BAD_REQUEST(4009, HttpStatus.BAD_REQUEST, "이미지 업로드 요청 방식이 올바르지 않습니다. 다시 시도해주세요."),
    IMAGE_UPLOAD_FAIL(4010, HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다. 다시 시도해주세요."),
    NOT_FOUND_IMAGE(4011, HttpStatus.BAD_REQUEST, "요청에서 이미지 파일을 찾을 수 없습니다. 다시 시도해주세요."),
    OVER_SIZE_IMAGE(4012, HttpStatus.BAD_REQUEST, "이미지 파일이 용량을 초과하여 업로드할 수 없습니다. 업로드 가능한 크기는 최대 10MB 입니다."),


    //- 5000번대 : 채팅방(chat) 관련 코드
    NOT_FOUND_CHAT(5000, HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),
    ALREADY_CHAT(5001, HttpStatus.OK, "이미 존재하는 채팅방입니다."),
    UNAUTHORIZED_CHAT_MEMBER(5002, HttpStatus.UNAUTHORIZED, "채팅방에 접근할 수 없는 회원입니다. 권한을 확인해주세요."),
    EMPTY_CHAT_MEMBER(5003, HttpStatus.BAD_REQUEST, "채팅방 구성원이 없습니다. 다시 시도해주세요."),
    NOT_FOUND_CHAT_MEMBER(5004, HttpStatus.BAD_REQUEST, "채팅방 구성원이 아닙니다."),
    ALREADY_LEFT_CHAT(5005, HttpStatus.BAD_REQUEST, "해당 회원은 이미 채팅방을 나갔습니다."),


    //- 6000번대 : 메시지(message)/소켓 관련 코드
    WEBSOCKET_CONNECTION_FAIL(6000, HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 서버 접속에 실패했습니다."),
    WEBSOCKET_CLOSE_FAIL(6001, HttpStatus.INTERNAL_SERVER_ERROR, "웹소켓 서버와의 연결 종료에 실패했습니다."),
    MESSAGE_FORMAT_ERROR(6002, HttpStatus.BAD_REQUEST, "메시지에서 하나 이상의 필드가 누락되었습니다. json 데이터를 확인해주세요."),
    CONVERT_TO_JSON_FAIL(6003, HttpStatus.INTERNAL_SERVER_ERROR, "메시지를 json 형태로 바꾸는데 실패했습니다"),
    CONVERT_TO_OBJECT_FAIL(6004, HttpStatus.INTERNAL_SERVER_ERROR, "json을 메세지 객체 형태로 바꾸는데 실패했습니다"),
    MESSAGE_SEND_FAIL(6005, HttpStatus.INTERNAL_SERVER_ERROR, "메시지 전송에 실패했습니다."),
    NOT_FOUND_MESSAGE(6006, HttpStatus.NOT_FOUND, "존재하지 않는 메세지입니다."),
    MESSAGE_LENGTH_MISMATCH(6007, HttpStatus.BAD_REQUEST, "메세지의 payload 길이가 일치하지 않습니다."),
    NOT_FOUND_SESSION(6008, HttpStatus.NOT_FOUND, "사용자의 웹소켓 세션이 존재하지 않습니다. 다시 연결을 시도해주세요."),

    //- 7000번대 : 친구, 차단 관련 코드
//    NOT_FOUND_FRIENDSHIP(7000, HttpStatus.NOT_FOUND, "차단하거나 차단 당한 상대의 프로필을 조회할 수 없습니다."),
    BLOCKED_MEMBER_PROFILE(7000, HttpStatus.BAD_REQUEST, "차단하거나 차단 당한 상대의 프로필을 조회할 수 없습니다."),
    ALREADY_FRIEND(7001, HttpStatus.BAD_REQUEST, "이미 친구 관계인 사이에서 친구 관계를 중복하여 맺을 수 없습니다"),
    BLOCKED_MEMBER_APPLY(7002, HttpStatus.BAD_REQUEST, "차단하거나 차단 당한 사용자는 친구신청을 보낼 수 없습니다."),
    NOT_FOUND_FRIEND(7003, HttpStatus.NOT_FOUND, "해당 사용자와의 친구 관계가 존재하지 않습니다."),
    NOT_FOUND_APPLY(7004, HttpStatus.NOT_FOUND, "친구 신청이 존재하지 않습니다."),
    ALREADY_BLOCK(7005, HttpStatus.BAD_REQUEST, "이미 차단했거나 차단당한 사용자끼리는 차단을 할 수 없습니다."),
    NOT_FOUND_PLUSID(7006, HttpStatus.NOT_FOUND, "해당 plus id를 사용하는 사용자가 존재하지 않습니다."),
    NOT_FOUND_BLOCK(7007, HttpStatus.NOT_FOUND, "해당 사용자들 사이에 차단 관계가 존재하지 않습니다."),


//    ALREADY_APPLY(70, HttpStatus.BAD_REQUEST, "둘 사이에 보낸 요청이 존재합니다."),

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
