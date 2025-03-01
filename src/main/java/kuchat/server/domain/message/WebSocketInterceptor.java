package kuchat.server.domain.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.KuchatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static kuchat.server.common.response.BaseResponseStatus.CONVERT_TO_JSON_FAIL;

@Component
@Slf4j
public class WebSocketInterceptor implements ChannelInterceptor, HandshakeInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        Object payload = message.getPayload();
        log.info("[preSend] payload = " + new String((byte[]) payload, StandardCharsets.UTF_8));
//        Map<String, String> payloadMap;
//
//        try {
//            payloadMap = objectMapper.readValue((byte[]) payload, new TypeReference<HashMap<String, String>>() {
//            });
//        } catch (JsonProcessingException e) {
//            throw new KuchatException(CONVERT_TO_JSON_FAIL);
//        }


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


        Map<String, String> payloadMap;

        try {
            payloadMap = objectMapper.readValue((String) payload, new TypeReference<HashMap<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new KuchatException(CONVERT_TO_JSON_FAIL);
        }


        log.info("[postReceive] 메시지 헤더 = {}", message.getHeaders());
        log.info("[postReceive] 메시지 내용 = {}", payloadMap);


        if (command == StompCommand.SUBSCRIBE) {
            log.info(accessor.getDestination());
        }
        return message;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("[beforeHandshake] 웹소켓 연결 성공");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("[beforeHandshake] 웹소켓 연결 성공2");
    }
}
