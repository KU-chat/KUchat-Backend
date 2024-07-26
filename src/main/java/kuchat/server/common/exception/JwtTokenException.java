package kuchat.server.common.exception;

public class JwtTokenException extends KuchatException{
    public JwtTokenException(BaseResponse baseResponse) {
        super(baseResponse);
    }
}
