package kuchat.server.common.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ErrorResponse {
//    private int code;
//    private HttpStatus httpStatus;
//    private String message;
    private BaseResponseStatus baseResponseStatus;

    public int getCode() {
        return baseResponseStatus.getCode();
    }

    public HttpStatus getHttpStatus() {
        return baseResponseStatus.getHttpStatus();
    }

    public String getMessage() {
        return baseResponseStatus.getMessage();
    }

    public void setMessage(String message) {
        baseResponseStatus.setMessage(message);
    }
}
