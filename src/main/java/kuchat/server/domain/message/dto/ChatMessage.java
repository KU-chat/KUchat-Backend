package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import lombok.*;


@Getter
@Builder
@AllArgsConstructor
@ToString
public class ChatMessage {
    private Long messageId;
    private Long chatroomId;
    private MessageType messageType;
    private Long senderId;
    private String text;
}
