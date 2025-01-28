package kuchat.server.domain.friend.dto;

import kuchat.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
public class FriendResponse {
    private Long memberId;
    private String name;
    private String profileImage;
    private String aboutMe;

    FriendResponse(Member member) {
        this.memberId = member.getId();
        this.name = member.getName();
        this.profileImage = member.getProfile().getProfileImage();
        this.aboutMe = member.getProfile().getAboutMe();
    }
}
