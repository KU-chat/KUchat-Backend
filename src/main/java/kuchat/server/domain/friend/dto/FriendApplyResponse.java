package kuchat.server.domain.friend.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import kuchat.server.domain.friend.Friend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
public class FriendApplyResponse {
    private Long friendId;
    private FriendProfile friendProfile;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applySentTime;

    FriendApplyResponse(Friend friend) {
        this.friendId = friend.getId();
        this.friendProfile = new FriendProfile(friend.getSender());
        this.applySentTime = friend.getCreatedDate();
    }
}
