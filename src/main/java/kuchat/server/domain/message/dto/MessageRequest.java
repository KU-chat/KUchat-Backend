package kuchat.server.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Objects;

@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    @NotBlank
    private Long chatId;      // 채팅방 ID

    @NotBlank
    private String messageType;        // 메시지 타입 (ENTER, CHAT, LEAVE)

    @NotBlank
    private Long senderId;

    @NotNull
    private String content;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageRequest that = (MessageRequest) o;
        return Objects.equals(getMessageType(), that.getMessageType()) &&
                Objects.equals(getSenderId(), that.getSenderId()) &&
                Objects.equals(getContent(), that.getContent()) &&
                Objects.equals(getChatId(), that.getChatId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageType(), getSenderId(), getContent(), getChatId());
    }
}
