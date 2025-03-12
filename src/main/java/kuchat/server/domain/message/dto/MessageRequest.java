package kuchat.server.domain.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import kuchat.server.domain.enums.MessageType;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MessageRequest {

    @JsonProperty("chatId")
    @NotNull
    private Long chatId;      // 채팅방 ID

    @JsonProperty("messageType")
    @NotNull
    private MessageType messageType;        // 메시지 타입 (ENTER, CHAT, LEAVE)

    @JsonProperty("senderId")
    @NotNull
    private Long senderId;

    @JsonProperty("content")
    @NotNull
    private String content;
}
