package kuchat.server.domain.block.dto;

import kuchat.server.common.exception.BaseResponse;
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
    BaseResponse baseResponse;

    public BlockMemberResponses(List<BlockMemberResponse> blockMemberResponses) {
        this.blockMemberResponses = blockMemberResponses;
        baseResponse = BaseResponse.SUCCESS;
    }
}
