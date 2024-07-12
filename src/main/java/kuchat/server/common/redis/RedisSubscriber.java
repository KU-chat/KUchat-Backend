package kuchat.server.common.redis;

import kuchat.server.domain.message.ChatMessageEvent;
import kuchat.server.domain.message.dto.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
public class RedisSubscriber implements MessageListener {
    private ApplicationEventPublisher eventPublisher;
    private RedisTemplate<String, Object> redisTemplate;
    private Long memberId;
    private Set<ChannelTopic> subscribeTopics = new HashSet<>();

    public RedisSubscriber(Long memberId, ApplicationEventPublisher eventPublisher, RedisTemplate<String, Object> redisTemplate) {
        this.memberId = memberId;
    }

    public void subscribeTo(ChannelTopic topic) {
        subscribeTopics.add(topic);
    }

    public void unsubscribeFrom(ChannelTopic topic) {
        subscribeTopics.remove(topic);
    }

    // 메세지가 발행(pub) 되면 onMessage가 자동호출되어 햐덩 메세지를 처리한다
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 발행된 메세지를 redis 로부터 받아 deserialize
        String publishMessage = redisTemplate.getStringSerializer().deserialize(message.getBody());
        log.info("[onMessage] redis 가 받은 메세지 = {}", publishMessage);
        ChatMessage chatMessage = new ChatMessage(publishMessage);
        ChannelTopic topic = new ChannelTopic("chatroom: " + chatMessage.getChatroomId());

        eventPublisher.publishEvent(new ChatMessageEvent(this, chatMessage));
//        sendingOperations.convertAndSend("/sub/chatroom/" + chatMessage.getChatroomId(), chatMessage);
    }
}
