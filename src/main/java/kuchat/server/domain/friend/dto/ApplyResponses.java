package kuchat.server.domain.friend.dto;

import kuchat.server.common.exception.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static kuchat.server.common.exception.BaseResponse.FRIEND_APPLY_LOOKUP_SUCCESS;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApplyResponses {
    private List<ApplyResponse> responses;
    private BaseResponse baseResponse;

    public ApplyResponses(List<ApplyResponse> responses) {
        this.responses = responses;
        baseResponse = FRIEND_APPLY_LOOKUP_SUCCESS;
    }
}
