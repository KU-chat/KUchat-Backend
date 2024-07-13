package kuchat.server.common.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.HashSet;
import java.util.Set;

public class Subscriber implements ChannelSubscriber {

    private Long memberId;
    private Set<ChannelTopic> subscribedChannels = new HashSet<>();

    public Subscriber(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public Set<ChannelTopic> getSubscribedChannels() {
        return subscribedChannels;
    }

    @Override
    public void subscribe(ChannelTopic channel) {
        subscribedChannels.add(channel);
    }

    @Override
    public void unsubscribe(ChannelTopic channel) {
        subscribedChannels.remove(channel);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("received: " + message.toString());
    }

}