package kuchat.server.domain.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.nio.charset.StandardCharsets;

@Slf4j
public class WebSocketInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        log.info("📩 [STOMP {} 요청 감지] destination={}, payload={}",
                    command.name(),
                    accessor.getDestination(),
                    message.getPayload());


        Object payload = message.getPayload();
        log.info("[preSend] payload = " + new String((byte[]) payload, StandardCharsets.UTF_8));
        log.info("[preSend] 메시지 헤더 = {}", message.getHeaders());
//        log.info("[preSend] 메시지 내용 = {}", payloadMap);

        if (command == StompCommand.SUBSCRIBE) {
            log.info(accessor.getDestination());
        }
        return message;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        Object payload = message.getPayload();
        log.info("[postReceive] " + payload.toString());
        log.info("[postReceive] 메시지 헤더 = {}", message.getHeaders());
        log.info("[postReceive] 메시지 내용 = {}", payload.toString());


        if (command == StompCommand.SUBSCRIBE) {
            log.info(accessor.getDestination());
        }
        return message;
    }

}
