package kuchat.server.common.redis;

import lombok.Getter;

@Getter
public enum RedisKey {
    REFRESH_TOKEN_KEY("refresh_token"),
    CONNECT_KEY("connect"),
    NO_READ_KEY("noReadNum"),
    GROUP_CHAT_KEY("isGroupChat"),
    LAST_MESSAGE_CONTENT_KEY("lastMessageContent"),
    LAST_MESSAGE_TIME_KEY("lastMessageTimestamp"),

    MEMBER_KEY("member_{}"),
    CHAT_KEY("chat_{}");

    private final String key;

    RedisKey(String key) {
        this.key = key;
    }

    public String getKey(Long id){
        return replaceId(this, id);
    }

    private static String replaceId(RedisKey redisKey, Long id) {
        return redisKey.getKey().replace("{}", id.toString());
    }
}
