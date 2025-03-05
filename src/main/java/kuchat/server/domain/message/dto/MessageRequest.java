package kuchat.server.domain.message.dto;

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
    @NotBlank
    private Long chatId;      // 채팅방 ID

    @NotBlank
    private String messageType;        // 메시지 타입 (ENTER, CHAT, LEAVE)

    @NotBlank
    private Long senderId;

    @NotNull
    private String content;
}
