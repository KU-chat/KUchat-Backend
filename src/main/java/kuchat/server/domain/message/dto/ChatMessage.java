package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.message.Message;
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
        this.parentId = null;
    }

    public ChatMessage(Message message){
        this.messageId = message.getMessageId().getMessageId();
        this.chatroomId = message.getMessageId().getChatroomId();
        this.messageType = message.getMessageType();
        this.senderId = message.getSenderId();
        this.text = message.getText();
        this.parentId = message.getParent().getMessageId().getMessageId();
    }
    
}
