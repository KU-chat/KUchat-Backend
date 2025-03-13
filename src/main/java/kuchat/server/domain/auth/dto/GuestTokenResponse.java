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
public class GuestTokenResponse extends BaseResponse {
    private String guestToken;

    public GuestTokenResponse(BaseResponseStatus responseStatus, String guestToken) {
        super(responseStatus);
        this.guestToken = guestToken;
    }
}
