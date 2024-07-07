package kuchat.server.domain.message;

import kuchat.server.domain.message.dto.ChatMessage;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class ChatMessageEvent extends ApplicationEvent {
    private WebSocketSession session;
    private ChatMessage chatMessage;

    public ChatMessageEvent(Object source,ChatMessage chatMessage) {
        super(source);
        this.chatMessage = chatMessage;
    }

}
