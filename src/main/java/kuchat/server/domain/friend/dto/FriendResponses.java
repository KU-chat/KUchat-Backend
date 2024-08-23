package kuchat.server.domain.friend.dto;

import kuchat.server.common.exception.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponses {
    private List<FriendResponse> responses;
    private BaseResponse baseResponse;

    public FriendResponses(List<FriendResponse> responses) {
        this.responses = responses;
        baseResponse = BaseResponse.SUCCESS;
    }
}
