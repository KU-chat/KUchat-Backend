//package kuchat.server.common.redis;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.stereotype.Service;
//
//@RequiredArgsConstructor
//@Service
//public class SubscriptionManager {
//    private final RedisMessageListenerContainer messageListener;
//
//    public void addSubscription(ChannelSubscriber subscriber, ChannelTopic channel){
//        subscriber.subscribe(channel);
//        messageListener.addMessageListener(subscriber, channel);
//    }
//
//    public void removeSubscription(ChannelSubscriber subscriber, ChannelTopic channel){
//        subscriber.unsubscribe(channel);
//        messageListener.removeMessageListener(subscriber, channel);
//    }
//}
