package kuchat.server.domain.auth.dto;

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
public class AuthTokenResponse extends BaseResponse {
    private String accessToken;
    private String refreshToken;

    public AuthTokenResponse(BaseResponseStatus responseStatus, String accessToken, String refreshToken) {
        super(responseStatus);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
