package kuchat.server.domain.member.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class AuthTokenResponse extends BaseResponse {
    private Long id;
    private String accessToken;
    private String refreshToken;

    public AuthTokenResponse(BaseResponseStatus responseStatus, Long id, String accessToken, String refreshToken) {
        this.responseStatus = responseStatus;
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
