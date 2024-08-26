package kuchat.server.domain.chatroom.dto;

import kuchat.server.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatroomResponse {
    private Long id;
    private BaseResponseStatus baseResponseStatus;
}
