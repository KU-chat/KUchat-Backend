package kuchat.server.domain.message;

import kuchat.server.domain.message.dto.ChatMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChatMessageEvent extends ApplicationEvent {
    private Object source;
    private ChatMessage message;

    public ChatMessageEvent(Object source, ChatMessage message) {
        super(source);
        this.message = message;
    }
}
