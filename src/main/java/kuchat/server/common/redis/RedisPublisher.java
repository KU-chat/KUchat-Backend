//package kuchat.server.common.redis;
//
//import kuchat.server.domain.message.dto.ChatMessage;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class RedisPublisher {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public void publish(ChannelTopic topic, ChatMessage chatMessage){
//        log.info("[publish] 발행된 메세지 = {}, topic = {}", chatMessage.toString(), topic.toString());
//        redisTemplate.convertAndSend(topic.getTopic(), chatMessage);
//    }
//}
