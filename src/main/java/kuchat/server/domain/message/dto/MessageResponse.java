package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.message.Message;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MessageResponse {
    private Long chatId;
    private Long senderId;
    private LocalDateTime createdAt;
    private String messageType;
    private String content;

    public MessageResponse(Message message){
        this.chatId = message.getChat().getId();
        this.senderId = message.getSender().getId();
        this.createdAt = message.getCreatedDate();
        this.messageType = message.getMessageType().name();
        this.content = message.getContent();
    }

}
