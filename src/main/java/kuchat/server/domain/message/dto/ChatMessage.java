package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChatMessage {

    private Long chatroomId;
    private MessageType messageType;
    private Long senderId;
    private String senderName;
    private String text;
    private String parentId;

    public ChatMessage(Long chatroomId, String type, Long senderId, String senderName, String text) {
        this.chatroomId = chatroomId;
        this.messageType = MessageType.valueOf(type);
        this.senderId = senderId;
        this.senderName = senderName;
        this.text = text;
    }

}
