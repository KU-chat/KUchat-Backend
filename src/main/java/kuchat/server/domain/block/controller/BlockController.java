package kuchat.server.domain.block.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.jwt.argumentResolver.Auth;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.block.dto.BlockMemberResponses;
import kuchat.server.domain.block.service.BlockService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.response.BaseResponseStatus.SUCCESS;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/block")
@RestController
public class BlockController {

    private final BlockService blockService;

    @Operation(summary = "사용자 차단하기")        // 친구가 아니었어도 차단 가능
    @PostMapping("/{id}")
    public ResponseEntity<BaseResponseStatus> block(@Auth Member member,
                                  @PathVariable("id") Long blockMemberId) {
        log.info("[blockMember] id = {} 인 사용자가 id = {} 인 사용자를 차단함. ", member.getId(), blockMemberId);
        blockService.block(member, blockMemberId);
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "사용자 차단 해제하기")
    @DeleteMapping("/{id}/release")
    public ResponseEntity<BaseResponseStatus> release(@Auth Member member,
                                                      @PathVariable("id") Long releaseMemberId) {
        log.info("[release] id = {} 인 사용자가 id = {} 인 사용자를 차단 해제함. ", member.getId(), releaseMemberId);
        blockService.release(member, releaseMemberId);
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "내가 차단한 사용자 목록 조회")
    @GetMapping("")
    public ResponseEntity<BlockMemberResponses> blockMemberList(@Auth Member member) {
        log.info("[blockMemberList] {} 가 차단한 사용자들 목록 조회", member.getId());
        BlockMemberResponses response = blockService.getblocks(member);
        return ResponseEntity.ok(response);
    }
}
