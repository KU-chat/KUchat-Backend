package kuchat.server.domain.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class WebSocketInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        String content = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
        log.info("[preSend] 메시지 헤더 = {}", message.getHeaders());
        log.info("[preSend] 메시지 내용 = {}", content);


        if (command == StompCommand.SUBSCRIBE) {
            log.info(accessor.getDestination());
        }
        return message;
    }
}
