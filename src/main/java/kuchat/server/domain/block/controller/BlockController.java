package kuchat.server.domain.block.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.jwt.argumentResolver.Auth;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.block.dto.BlockMemberResponses;
import kuchat.server.domain.block.service.BlockService;
import kuchat.server.domain.friend.service.FriendService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/block")
@RestController
public class BlockController {

    private final BlockService blockService;
    private final FriendService friendService;

    @Operation(summary = "사용자 차단하기")        // 친구가 아니었어도 차단 가능
    @PostMapping("/{memberId}")
    public ResponseEntity<BaseResponse> block(@Auth Member member,
                                              @PathVariable("memberId") Long blockMemberId) {
        log.info("[blockMember] id = {} 인 사용자가 id = {} 인 사용자를 차단함. ", member.getId(), blockMemberId);
        friendService.deleteIfFriend(member.getId(), blockMemberId);
        return blockService.block(member, blockMemberId);
    }

    @Operation(summary = "사용자 차단 해제하기")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<BaseResponse> release(@Auth Member member,
                                                @PathVariable("memberId") Long releaseMemberId) {
        log.info("[release] id = {} 인 사용자가 id = {} 인 사용자를 차단 해제함. ", member.getId(), releaseMemberId);
        return blockService.release(member, releaseMemberId);
    }

    @Operation(summary = "내가 차단한 사용자 목록 조회")
    @GetMapping
    public ResponseEntity<BlockMemberResponses> blockMemberList(@Auth Member member,
                                                                @PageableDefault(size = 20,
                                                                        sort = "createdDate",
                                                                        direction = Sort.Direction.DESC)
                                                                Pageable pageable) {
        log.info("[blockMemberList] {} 가 차단한 사용자들 목록 조회", member.getId());
        return blockService.getblocks(member, pageable);
    }
}
