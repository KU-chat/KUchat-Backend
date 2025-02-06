package kuchat.server.domain.message.dto;

import lombok.*;

import java.util.Objects;

@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
    private String type;        // 메시지 타입 (ENTER, CHAT, LEAVE)
    private String senderId;
    private String content;
    private String chatId;      // 채팅방 ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageRequest that = (ChatMessageRequest) o;
        return Objects.equals(getType(), that.getType()) &&
                Objects.equals(getSenderId(), that.getSenderId()) &&
                Objects.equals(getContent(), that.getContent()) &&
                Objects.equals(getChatId(), that.getChatId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getSenderId(), getContent(), getChatId());
    }
}
