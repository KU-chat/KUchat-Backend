package kuchat.server.domain.friend.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponses extends BaseResponse {
    private List<FriendResponse> responses;
    private BaseResponseStatus baseResponseStatus;

    public FriendResponses(BaseResponseStatus responseStatus, List<FriendResponse> responses) {
        super(responseStatus);
        this.responses = responses;
        baseResponseStatus = BaseResponseStatus.SUCCESS;
    }
}
