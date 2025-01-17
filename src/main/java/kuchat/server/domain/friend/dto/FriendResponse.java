package kuchat.server.domain.friend.dto;

import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FriendResponse {
    private Long friendId;
    private ProfileResponse profile;
    private LocalDateTime sentTime;

    public FriendResponse(Member member, LocalDateTime sentTime) {
        friendId = member.getId();
        this.profile = new ProfileResponse(member.getProfile());
        this.sentTime = sentTime;
    }

    public FriendResponse(Member member) {
        friendId = member.getId();
        this.profile = new ProfileResponse(member.getProfile());
    }
}
