package kuchat.server.domain.chatroom.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateChatroomRequest {
    private String newName;
}
