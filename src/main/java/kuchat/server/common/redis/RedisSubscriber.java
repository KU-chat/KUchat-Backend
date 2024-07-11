package kuchat.server.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.message.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessageSendingOperations sendingOperations;

    // 메세지가 발행(pub) 되면 onMessage가 자동호출되어 햐덩 메세지를 처리한다
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 발행된 메세지를 redis 로부터 받아 deserialize
        String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
        log.info("[onMessage] redis 가 받은 메세지 = {}", publishMessage);
        ChatMessage chatMessage = new ChatMessage(publishMessage);
        sendingOperations.convertAndSend("/sub/chatroom/"+chatMessage.getChatroomId(), chatMessage);
    }
}
