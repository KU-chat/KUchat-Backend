package kuchat.server.common.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class SimpleErrorResponse extends BaseResponse {

    public SimpleErrorResponse(BaseResponseStatus responseStatus) {
        super(responseStatus);
    }
}
