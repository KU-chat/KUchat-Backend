package kuchat.server.domain.friend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.member.dto.DetailProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@ToString
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class FriendApplyResponse {
    private Long friendApplyId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applySentTime;

    private ApplySenderProfile applySenderProfile;

    public FriendApplyResponse(Friend friend){
        this.friendApplyId = friend.getId();
        this.applySenderProfile = new ApplySenderProfile(friend.getSender());
        this.applySentTime = friend.getCreatedDate();
    }
}
