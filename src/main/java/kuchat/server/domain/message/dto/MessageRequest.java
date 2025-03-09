package kuchat.server.domain.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MessageRequest {

    @JsonProperty("chatId")
    @NotBlank
    private Long chatId;      // 채팅방 ID

    @JsonProperty("messageType")
    @NotBlank
    private String messageType;        // 메시지 타입 (ENTER, CHAT, LEAVE)

    @JsonProperty("senderId")
    @NotBlank
    private Long senderId;

    @JsonProperty("content")
    @NotNull
    private String content;
}
