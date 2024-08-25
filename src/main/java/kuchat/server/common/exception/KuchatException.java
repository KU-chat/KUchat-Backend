package kuchat.server.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
//@RequiredArgsConstructor
public class KuchatException extends RuntimeException {
    private HttpStatus httpStatus;
    private String message;
    private int code;

    public KuchatException(BaseResponse baseResponse){
        this.httpStatus = baseResponse.getHttpStatus();
        this.message = baseResponse.getMessage();
        this.code = baseResponse.getCode();
    }

    public KuchatException(BaseResponse baseResponse,String message){
        this.httpStatus = baseResponse.getHttpStatus();
        this.message = message;
        this.code = baseResponse.getCode();
    }
}
