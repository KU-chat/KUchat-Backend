package kuchat.server.domain.chatroom.dto;

import kuchat.server.common.exception.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatroomResponse {
    private Long id;
    private BaseResponse baseResponse;
}
