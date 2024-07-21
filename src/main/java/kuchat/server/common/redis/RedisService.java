package kuchat.server.common.redis;

import jakarta.annotation.PostConstruct;
import kuchat.server.domain.message.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisService {
    private final SubscriptionManager subscriptionManager;
    private final SimpMessageSendingOperations messageSendingOperations;

    private Map<Long, Subscriber> subscribers;
    private Map<Long, ChannelTopic> channels;         // chatroom id - ChannelTopic
    private Map<Long, Map<String, String>> memberInfo;      // member id - refresh token, name

    @PostConstruct
    private void init() {
        memberInfo = new ConcurrentHashMap<>();
//        subscribers = new ConcurrentHashMap<>();
//        channels = new ConcurrentHashMap<>();
    }

    public void registerMember(Long memberId, String name, String refreshToken, String profileImage){
        Map<String, String> info = new HashMap<>();
        info.put("name", name);
        info.put("refreshToken", refreshToken);
        info.put("profileImage", profileImage);
        memberInfo.put(memberId, info);
    }

    public String getRefreshToken(Long memberId){
        return memberInfo.get(memberId).get("refreshToken");
    }

    public String getMemberName(Long memberId){
        return memberInfo.get(memberId).get("name");
    }

    public String getProfileImage(Long memberId){
        return memberInfo.get(memberId).get("profileImage");
    }

    public void setMemberName(Long memberId, String name){
        memberInfo.get(memberId).put("name", name);
    }

    public void setRefreshToken(Long memberId, String refreshToken){
        memberInfo.get(memberId).put("refreshToken", refreshToken);
    }

    public void setProfileImage(Long memberId, String profileImage){
        memberInfo.get(memberId).put("profileImage", profileImage);
    }

    // topic 구독
//    public void subscribeTopic(Long chatroomId, List<Long> memberIds) {
//        log.info("[subscribeTopic] {} 회원들이 {} 번 채팅방 구독함.", memberIds.toString(), chatroomId);
//        ChannelTopic channel = getChannel(chatroomId);
//
//        for (Long memberId : memberIds) {
//            Subscriber subscriber = getSubscriber(memberId);
//            subscriptionManager.addSubscription(subscriber, channel);
//        }
//    }
//
//
//    // topic 구독 취소
//    public void cancelSubscribe(Long chatroomId, Long memberId) {
//        log.info("[cancelSubscribe] {} 회원이 {}번 채팅방 구독 취소함.", memberId, chatroomId);
//        ChannelTopic channel = getChannel(chatroomId);
//        Subscriber subscriber = getSubscriber(memberId);
//        subscriptionManager.removeSubscription(subscriber, channel);
//    }
//
//
//    private Subscriber getSubscriber(Long memberId) {
//        Subscriber subscriber = subscribers.get(memberId);
//        if (subscriber == null) {
//            subscriber = new Subscriber(memberId);
//            subscribers.put(memberId, subscriber);
//        }
//        return subscriber;
//    }
//
//
//    public ChannelTopic getChannel(Long chatroomId) {
//        ChannelTopic channel = channels.get(chatroomId);
//        if (channel == null) {
//            channel = new ChannelTopic("/sub/chatroom/" + chatroomId);
//            channels.put(chatroomId, channel);
//        }
//
//        return channel;
//    }
//
//    public void send(String topic, MessageResponse messageResponse) {
//        messageSendingOperations.convertAndSend(topic, messageResponse);
//    }
}
