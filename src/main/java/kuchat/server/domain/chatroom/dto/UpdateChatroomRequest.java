package kuchat.server.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateChatroomRequest {
    private String newName;
}
