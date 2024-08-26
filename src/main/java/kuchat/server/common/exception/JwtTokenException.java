package kuchat.server.common.exception;

import kuchat.server.common.response.BaseResponseStatus;

public class JwtTokenException extends KuchatException{
    public JwtTokenException(BaseResponseStatus baseResponseStatus) {
        super(baseResponseStatus);
    }
}
