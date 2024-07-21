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
    private String senderName;
    private String senderImage;
    private String text;
    private Long parentId;
    private LocalDateTime createdDate;

    @Builder
    public MessageResponse(Message message, String senderName, String senderImage) {
        this.messageId = message.getMessageId();
        this.chatroomId = message.getMessageId();
        this.messageType = message.getMessageType();
        this.senderId = message.getSenderId();
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.text = message.getText();
        this.parentId = ((message.getParent()) != null) ? message.getParent().getMessageId() : null;
        this.createdDate = message.getCreatedDate();
    }

    public static MessageResponse serverNotice(Message message) {
        return MessageResponse.builder()
                .message(message)
                .senderName(null)
                .senderImage(null)
                .build();

//        this.messageId = message.getMessageId();
//        this.chatroomId = message.getMessageId();
//        this.messageType = message.getMessageType();
//        this.senderId = message.getSenderId();
//        this.text = message.getText();
//        this.parentId = null;
//        this.createdDate = message.getCreatedDate();
    }

}
