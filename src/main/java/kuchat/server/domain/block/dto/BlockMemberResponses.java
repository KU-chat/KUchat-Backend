package kuchat.server.domain.block.dto;

import kuchat.server.common.response.BaseResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BlockMemberResponses {
    List<BlockMemberResponse> blockMemberResponses;
    BaseResponseStatus baseResponseStatus;

    public BlockMemberResponses(List<BlockMemberResponse> blockMemberResponses) {
        this.blockMemberResponses = blockMemberResponses;
        baseResponseStatus = BaseResponseStatus.SUCCESS;
    }
}
