package kuchat.server.common.exception;

import kuchat.server.common.response.BaseResponseStatus;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
//@RequiredArgsConstructor
public class KuchatException extends RuntimeException {

    private BaseResponseStatus baseResponseStatus;

    public KuchatException(BaseResponseStatus baseResponseStatus){
        this.baseResponseStatus = baseResponseStatus;
    }

    public KuchatException(BaseResponseStatus baseResponseStatus, String message){
        this.baseResponseStatus = baseResponseStatus;
        baseResponseStatus.setMessage(message);
    }

    public HttpStatus getHttpStatus(){
        return baseResponseStatus.getHttpStatus();
    }
}
