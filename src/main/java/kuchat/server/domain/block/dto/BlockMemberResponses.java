package kuchat.server.domain.block.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.block.Block;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BlockMemberResponses extends BaseResponse {
    List<BlockMemberResponse> responses;

    public BlockMemberResponses(BaseResponseStatus responseStatus, Page<Block> blocks) {
        super(responseStatus);
        this.responses = blocks.stream()
                .map(BlockMemberResponse::new)
                .toList();
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BlockMemberResponse {
        private Long memberId;
        private ProfileResponse profile;

        public BlockMemberResponse(Block block) {
            Member member = block.getBlocked();
            memberId = member.getId();
            this.profile = new ProfileResponse(member.getProfile());
        }
    }
}
