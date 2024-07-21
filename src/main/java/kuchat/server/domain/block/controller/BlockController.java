package kuchat.server.domain.block.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.argumentResolver.Auth;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.block.dto.BlockMemberResponses;
import kuchat.server.domain.block.service.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.exception.BaseResponse.BLOCK_MEMBER_SUCCESS;
import static kuchat.server.common.exception.BaseResponse.RELEASE_BLOCK_SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/block")
@RestController
public class BlockController {

    private final BlockService blockService;

    @Operation(summary = "사용자 차단하기")        // 친구가 아니었어도 차단 가능
    @PostMapping("/{id}")
    public ResponseEntity<BaseResponse> block(@Auth Long memberId,
                                              @RequestParam("id") Long blockMemberId) {
        log.info("[blockMember] id = {} 인 사용자가 id = {} 인 사용자를 차단함. ", memberId, blockMemberId);
        blockService.block(memberId, blockMemberId);
        return ResponseEntity.ok(BLOCK_MEMBER_SUCCESS);
    }

    @Operation(summary = "사용자 차단 해제하기")
    @DeleteMapping("/{id}/release")
    public ResponseEntity<BaseResponse> release(@Auth Long memberId,
                                                @PathVariable("id") Long releaseMemberId) {
        log.info("[release] id = {} 인 사용자가 id = {} 인 사용자를 차단 해제함. ", memberId, releaseMemberId);
        blockService.release(memberId, releaseMemberId);
        return ResponseEntity.ok(RELEASE_BLOCK_SUCCESS);
    }

    @Operation(summary = "내가 차단한 사용자 목록 조회")
    @GetMapping("")
    public ResponseEntity<BlockMemberResponses> blockMemberList(@Auth Long memberId) {
        log.info("[blockMemberList] {} 가 차단한 사용자들 목록 조회", memberId);
        BlockMemberResponses response = blockService.getblocks(memberId);
        return ResponseEntity.ok(response);
    }
}
