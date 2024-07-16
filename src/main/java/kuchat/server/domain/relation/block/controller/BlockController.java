package kuchat.server.domain.relation.block.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.relation.block.service.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<BaseResponse> blockMember(@AuthenticationPrincipal Member member,
                                                    @RequestParam("id") Long blockMemberId) {
        log.info("[blockMember] {} 사용자가 {} 사용자를 차단함. ", member.getId(), blockMemberId);
        blockService.block(member, blockMemberId);
        return ResponseEntity.ok(BLOCK_MEMBER_SUCCESS);
    }

    @Operation(summary = "사용자 차단 해제하기")
    @DeleteMapping("/{id}/release")
    public ResponseEntity<BaseResponse> unblockMember(@AuthenticationPrincipal Member member,
                                                      @PathVariable("id") Long blockMemberId) {
        log.info("[unblockMember] {} 사용자가 {} 사용자의 차단을 해제함. ", member.getId(), blockMemberId);
        blockService.release(member, blockMemberId);
        return ResponseEntity.ok(RELEASE_BLOCK_SUCCESS);
    }

    @Operation(summary = "내가 차단한 사용자 목록 조회")
    @GetMapping("")
    public ResponseEntity<FriendResponses> blockMemberList(@AuthenticationPrincipal Member member) {
        log.info("[blockMemberList] {} 가 차단한 사용자들 목록 조회", member.getName());
        FriendResponses response = blockService.getblocks(member);
        return ResponseEntity.ok(response);
    }
}
