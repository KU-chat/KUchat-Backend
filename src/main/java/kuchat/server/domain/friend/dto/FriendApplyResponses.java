package kuchat.server.domain.friend.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.friend.Friend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
public class FriendApplyResponses extends BaseResponse {
    private List<FriendApplyResponse> applyResponses;

    public FriendApplyResponses(BaseResponseStatus responseStatus, Page<Friend> friends) {
        super(responseStatus);
        this.applyResponses = friends.stream()
                .map(FriendApplyResponse::new)
                .toList();
    }

}
