package kuchat.server.common.redis;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RedisMemberChatInfo implements Serializable {
    private String connect;
    private Long noReadNum;
}
