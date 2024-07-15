package kuchat.server.domain.chatroom.dto;

import kuchat.server.common.exception.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FindChatroomsResponse {
    private List<FindChatroomResponse> chatrooms;
    private BaseResponse baseResponse;
}
