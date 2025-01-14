package kuchat.server.common.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ErrorResponse extends BaseResponse {
    private List<Error> errors;

    public ErrorResponse(BaseResponseStatus responseStatus, List<Error> errors) {
        this.responseStatus = responseStatus;
        this.errors = errors;
    }
}
