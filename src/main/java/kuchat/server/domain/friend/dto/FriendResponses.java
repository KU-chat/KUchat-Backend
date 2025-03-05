package kuchat.server.domain.friend.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.LanguageResponse;
import kuchat.server.domain.member.dto.ProfileResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
@Slf4j
@NoArgsConstructor
public class FriendResponses extends BaseResponse {
    private List<FriendResponse> responses;

    public FriendResponses(BaseResponseStatus responseStatus, Page<Friend> friends) {
        super(responseStatus);
        this.responses = friends.stream()
                .map(friend -> new FriendResponse(friend.getReceiver()))
                .toList();
    }

    @Getter
    @Setter
    @ToString
    @Slf4j
    @NoArgsConstructor
    public static class FriendResponse {
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
}
