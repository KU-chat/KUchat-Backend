package kuchat.server.domain.chatroom.dto;

import kuchat.server.domain.chatroom.Chatroom;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class FindChatroomResponse {
    private Long id;
    private String name;

    public FindChatroomResponse(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public FindChatroomResponse(Page<Chatroom> chatroomPage){

    }
}
