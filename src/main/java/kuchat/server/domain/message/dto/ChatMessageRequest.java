package kuchat.server.domain.message.dto;

import lombok.*;

import java.util.Objects;

@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
    private String sender;
    private String receiver;
    private String content;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ChatMessageRequest) obj;
        return Objects.equals(this.sender, that.sender) &&
                Objects.equals(this.receiver, that.receiver) &&
                Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver, content);
    }

}
