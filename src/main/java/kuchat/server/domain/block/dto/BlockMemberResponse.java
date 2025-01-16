package kuchat.server.domain.block.dto;

import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BlockMemberResponse {
    private Long friendId;
    private ProfileResponse profile;
    private String name;
    private String profileImage;

    public BlockMemberResponse(Member member) {
        friendId = member.getId();
        this.profile = new ProfileResponse(member.getProfile());
    }
}
