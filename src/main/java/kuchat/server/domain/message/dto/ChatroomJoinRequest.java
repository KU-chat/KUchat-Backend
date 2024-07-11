package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import lombok.*;

import java.util.List;

import static kuchat.server.domain.message.service.MessageService.SERVER_ID;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomJoinRequest extends ChatMessage{
    private List<Long> memberIds;
    private Long chatroomId;
    private Long senderId = SERVER_ID;
    private MessageType messageType = MessageType.JOIN;
    private String text;

//    @Builder
    public ChatroomJoinRequest(Long chatroomId, List<Long> memberIds, String text) {
        this.chatroomId = chatroomId;
        this.memberIds = memberIds;
        this.text = text;
    }
}
