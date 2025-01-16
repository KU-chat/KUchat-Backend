package kuchat.server.common.jwt;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse extends BaseResponse {
    private AuthToken authToken;

    public TokenResponse(BaseResponseStatus responseStatus, AuthToken authToken) {
        this.responseStatus = responseStatus;
        this.authToken = authToken;
    }
}
