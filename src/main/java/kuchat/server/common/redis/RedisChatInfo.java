package kuchat.server.common.redis;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RedisChatInfo implements Serializable {
    private boolean isGroupChat;
    private int participantNum;
    private String lastMessageContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageTimestamp;
}
