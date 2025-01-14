package kuchat.server.domain.member.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class SignupResponse extends BaseResponse {
    private Long id;
    private String accessToken;
    private String refreshToken;

    public SignupResponse(BaseResponseStatus responseStatus, Long id, String accessToken, String refreshToken) {
        this.responseStatus = responseStatus;
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
