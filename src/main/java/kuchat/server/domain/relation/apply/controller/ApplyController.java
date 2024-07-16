package kuchat.server.domain.relation.apply.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.friend.dto.FriendRequest;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.relation.apply.service.ApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.exception.BaseResponse.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/apply")
@RestController
public class ApplyController {

    private final ApplyService applyService;

    @Operation(summary = "친구 프로필에서 친구 신청 보내기")
    @PostMapping("")
    public ResponseEntity<BaseResponse> sendApply(@AuthenticationPrincipal Member member,
                                                    @RequestBody FriendRequest request){
        log.info("[applyFriend] 친구 신청 요청 = {}", request.toString());
        applyService.send(member, request);
        return ResponseEntity.ok(FRIEND_APPLY_SUCCESS);
    }

    @Operation(summary = "plus id로 친구 신청 보내기")
    @PostMapping("/{plusId}")
    public ResponseEntity<BaseResponse> sendApplyByPlusId(@PathVariable("plusId") String plusId,
                                                            @AuthenticationPrincipal Member member){
        log.info("[applyFriendByPlusId] plusId = {} 인 친구에서 {} 가 친구 요청을 보냄", plusId, member.getId());
        applyService.applyByPlusId(member, plusId);
        return ResponseEntity.ok(FRIEND_APPLY_SUCCESS);
    }

    @Operation(summary = "내가 보낸 친구 신청 목록 조회")
    @GetMapping("/sent-request")
    public ResponseEntity<FriendResponses> getSentApply(@AuthenticationPrincipal Member member){
        log.info("[getSentApply] {} 가 보낸 친구 신청 목록 조회", member.getId());
        FriendResponses response = applyService.getSentApply(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 받은 친구 신청 목록 조회")
    @GetMapping("/received-request")
    public ResponseEntity<FriendResponses> getReceivedApply(@AuthenticationPrincipal Member member) {
        log.info("[getReceivedApply] {} 가 받은 친구 신청 목록 조회", member.getId());
        FriendResponses response = applyService.getReceivedApply(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 신청 수락")
    @PatchMapping("/accept/{id}")
    public ResponseEntity<BaseResponse> acceptApply(@AuthenticationPrincipal Member member,
                                                     @RequestParam("id") Long friendId){
        log.info("[acceptFriend] {}번 친구의 친구 신청 수락", friendId);
        applyService.accept(member, friendId);
        return ResponseEntity.ok(FRIEND_ACCEPT_SUCCESS);
    }

    @Operation(summary = "친구 신청 거절 (삭제)")
    @DeleteMapping("/refuse/{id}")
    public ResponseEntity<BaseResponse> refuseApply(@AuthenticationPrincipal Member member,
                                                    @RequestParam("id") Long friendId){
        log.info("[refuseApply] {}번 친구의 친구 신청 거절 ", friendId);
        applyService.refuse(member, friendId);
        return ResponseEntity.ok(FRIEND_APPLY_REFUSE_SUCCESS);
    }

}
