package kuchat.server.domain.friend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor
public class FriendApplyResponses extends BaseResponse {
    private List<FriendApplyResponse> applyResponses;

    public FriendApplyResponses(BaseResponseStatus responseStatus, Page<Friend> friends) {
        super(responseStatus);
        this.applyResponses = friends.stream()
                .map(FriendApplyResponse::new)
                .toList();
    }

    @Getter
    @Setter
    @ToString
    @Slf4j
    @NoArgsConstructor
    public static class FriendApplyResponse {
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

    @Getter
    @ToString
    @Slf4j
    @NoArgsConstructor
    public static class FriendProfile {
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
}
