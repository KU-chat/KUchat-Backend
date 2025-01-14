package kuchat.server.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.common.response.SimpleErrorResponse;
import lombok.Getter;

@Getter
public class KuchatException extends RuntimeException {

    private BaseResponse response;

    public KuchatException(BaseResponse response) {
        this.response = response;
    }

    public KuchatException(BaseResponseStatus responseStatus) {
        response = new SimpleErrorResponse(responseStatus);
    }
}
