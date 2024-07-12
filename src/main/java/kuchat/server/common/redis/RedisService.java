package kuchat.server.common.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
//@Component
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessageListenerContainer messageListener;
    private final ApplicationEventPublisher eventPublisher;

    private Map<Long, RedisSubscriber> subscribers;
    private Map<Long, ChannelTopic> topics;         // chatroom id - ChannelTopic
    private Map<ChannelTopic, Set<RedisSubscriber>> topicSub;     // 한 채팅방의 topic - 그걸 구독하고 있는 sub 들 집합

    @PostConstruct
    private void init() {
        subscribers = new HashMap<>();
        topics = new HashMap<>();
        topicSub = new ConcurrentHashMap<>();
    }

    // topic 구독
    public void subscribeTopic(Long chatroomId, List<Long> memberIds) {
        log.info("[subscribeTopic] {} 회원들이 {} 번 채팅방 구독함.", memberIds.toString(), chatroomId);
        ChannelTopic topic = getTopic(chatroomId);
        if(!topicSub.containsKey(topic)){
            topicSub.put(topic, ConcurrentHashMap.newKeySet());
        }

        for(Long memberId : memberIds){
            RedisSubscriber subscriber = getSubscriber(memberId);
            if(topicSub.get(topic).add(subscriber)){
                subscriber.subscribeTo(topic);
                messageListener.addMessageListener(subscriber, topic);
            }
        }
        topics.put(chatroomId, topic);
    }


    // topic 구독 취소
    public void cancelSubscribe(Long chatroomId, Long memberId) {
        log.info("[cancelSubscribe] {} 회원이 {}번 채팅방 구독 취소함.", memberId, chatroomId);
        ChannelTopic topic = getTopic(chatroomId);

        RedisSubscriber subscriber = getSubscriber(memberId);
        if(topicSub.containsKey(topic) && topicSub.get(topic).remove(subscriber)){
            subscriber.unsubscribeFrom(topic);
            messageListener.removeMessageListener(subscriber, topic);
        }
    }


    private RedisSubscriber getSubscriber(Long memberId){
        RedisSubscriber subscriber = subscribers.get(memberId);
        if (subscriber == null) {
            subscriber = new RedisSubscriber(memberId, eventPublisher, redisTemplate);
            subscribers.put(memberId, subscriber);
        }
        return subscriber;
    }


    public ChannelTopic getTopic(Long chatroomId) {
        ChannelTopic topic = topics.get(chatroomId);
        if (topic == null) {
            topic = new ChannelTopic("chatroom: " + chatroomId);
            topics.put(chatroomId, topic);
        }

        return topic;
    }

    public Set<RedisSubscriber> getSubscriberSet(Long memberId){
        Set<RedisSubscriber> subscriberSet = topicSub.get(memberId);
        if(subscriberSet == null){
            subscriberSet = new HashSet<>();
            subscriberSet.add(getSubscriber(memberId));
        }
        return subscriberSet;
    }

}
