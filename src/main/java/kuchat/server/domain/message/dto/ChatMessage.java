package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import lombok.*;


@Getter
@AllArgsConstructor
@ToString
public class ChatMessage {
    private Long messageId;
    private Long chatroomId;
    private MessageType messageType;
    private Long senderId;
    private String text;
    private Long parentId;

    @Builder
    public ChatMessage(Long messageId, Long chatroomId, MessageType messageType, Long senderId, String text){
        this.messageId = messageId;
        this.chatroomId = chatroomId;
        this.messageType = messageType;
        this.senderId = senderId;
        this.text = text;
    }
}
