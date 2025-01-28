package kuchat.server.domain.block.dto;

import kuchat.server.domain.block.Block;
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
    private Long memberId;
    private ProfileResponse profile;

    public BlockMemberResponse(Block block) {
        Member member = block.getBlocked();
        memberId = member.getId();
        this.profile = new ProfileResponse(member.getProfile());
    }
}
