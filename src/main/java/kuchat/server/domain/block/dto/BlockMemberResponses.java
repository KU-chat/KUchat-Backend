package kuchat.server.domain.block.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.block.Block;
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
}
