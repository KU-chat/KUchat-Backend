package kuchat.server.common.exception;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ErrorResponse {
    private int code;
    private String message;

    public ErrorResponse(BaseResponse baseResponse) {
        code = baseResponse.getCode();
        message = baseResponse.getMessage();
    }
}
