package kuchat.server.domain.message.dto;

import kuchat.server.domain.enums.MessageType;
import lombok.*;


@Getter
@AllArgsConstructor
@ToString
public class ChatMessage {
    private MessageType messageType;
    private Long roomId;
    private Long sender;
    @Setter
    private String text;
}
