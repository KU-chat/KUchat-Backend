package kuchat.server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendAcceptRequest {
    private Long senderId;
    private Long receiverId;
    private LocalDateTime sentTime;
}
