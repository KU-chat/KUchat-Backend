package kuchat.server.common.redis;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.Set;

public interface ChannelSubscriber extends MessageListener {
    Set<ChannelTopic> getSubscribedChannels();
    void subscribe(ChannelTopic channel);
    void unsubscribe(ChannelTopic channel);
}
