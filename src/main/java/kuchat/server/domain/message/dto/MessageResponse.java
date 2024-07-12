package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.message.Message;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


@Slf4j
@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long messageId;
    private Long chatroomId;
    private MessageType messageType;
    private Long senderId;
    private String text;
    private Long parentId;
    private LocalDateTime createdDate;

    public MessageResponse(Message message) {
        this.messageId = message.getMessageId();
        this.chatroomId = message.getMessageId();
        this.messageType = message.getMessageType();
        this.senderId = message.getSenderId();
        this.text = message.getText();
        this.parentId = ((message.getParent()) != null) ? message.getParent().getMessageId() : null;
        this.createdDate = message.getCreatedDate();
    }

}
