package kuchat.server.domain.chatroom.dto;

import lombok.Getter;

@Getter
public class FindChatroomResponse {
    private Long id;
    private String name;

    public FindChatroomResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
