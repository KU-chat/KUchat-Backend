package kuchat.server.domain.friend.dto;

import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.LanguageResponse;
import kuchat.server.domain.member.dto.ProfileResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Getter
@ToString
@Slf4j
@NoArgsConstructor
public class FriendProfile {
    private Long memberId;
    private String plusId;
    private LanguageResponse language;
    private ProfileResponse profile;


    public FriendProfile(Member member) {
        memberId = member.getId();
        plusId = member.getPlusId();
        profile = new ProfileResponse(member.getProfile());
        language = new LanguageResponse(member.getLanguage());
    }
}
