package kuchat.server.domain.message;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MessageId implements Serializable {
    private Long messageId;
    private Long chatroomId;        // @MapsId("chatroomId") 로 매핑

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageId messageId = (MessageId) o;
        return Objects.equals(messageId, messageId.messageId) &&
                Objects.equals(chatroomId, messageId.chatroomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, chatroomId);
    }
}
