package kuchat.server.domain.chatroom.dto;

import kuchat.server.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FindChatroomsResponse {
    private List<FindChatroomResponse> chatrooms;
    private BaseResponseStatus baseResponseStatus;
}
