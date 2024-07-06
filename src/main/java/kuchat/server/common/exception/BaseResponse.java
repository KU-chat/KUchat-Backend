package kuchat.server.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseResponse {
    //- 1000 번대 : jwt 관련 상태 코드
    TOKEN_PROVIDE_SUCCESS(1000, HttpStatus.OK, "jwt 토큰 발급 성공"),
    MALFORMED_TOKEN(1001, HttpStatus.UNAUTHORIZED, "토큰이 올바르게 구성되지 않았습니다."),

    //- 2000 번대 : oauth 관련 상태 코드
    OAUTH_SUCCESS(2000, HttpStatus.OK, "oauth 인증 성공"),
    PLATFORM_NOTFOUND(2001, HttpStatus.NOT_FOUND, "존재하지 않는 플랫폼입니다."),
    OAUTH2_FAIL(2002, HttpStatus.INTERNAL_SERVER_ERROR, "소셜로그인이 제대로 처리되지 않았습니다. 다시 시도해주세요."),

    //- 3000 번대 : 회원가입/멤버 관련 상태 코드
    SIGNUP_SUCCESS(3000, HttpStatus.CREATED, "회원가입 성공"),
    LANGUAGE_NOTFOUND(3001, HttpStatus.NOT_FOUND, "존재하지 않는 언어입니다."),
    MEMBER_NOTFOUND(3002, HttpStatus.NOT_FOUND, "존재하지 않는 회웝입니다."),
    EMAIL_BADREQUEST(3003, HttpStatus.BAD_REQUEST, "건국대학교 이메일이 아닙니다."),

    //- 4000번대 : 채팅방(chatroom) 관련 코드
    CHATROOM_SUCCESS(4000, HttpStatus.ACCEPTED, "채팅방 생성/수정 성공"),
    CHATROOM_NOTFOUND(4001, HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),

    //- 5000번대 : 메시지(message) 관련 코드

    //- 6000번대 : 친구 관련 코드

    //- 7000번대 : 알림 관련 코드


    //- 8000번대 : DB 관련 코드
    DB_SUCCESS(8000, HttpStatus.ACCEPTED,"DB에 성공적으로 반영되었습니다."),
    DB_SAVE_FAIL(8001, HttpStatus.INTERNAL_SERVER_ERROR, "DB 저장에 실패했습니다.");

    private int code;
    private HttpStatus httpStatus;
    private String message;


    BaseResponse(int code, HttpStatus httpStatus, String message){
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
