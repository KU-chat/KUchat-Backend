package kuchat.server.domain.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.friend.dto.*;
import kuchat.server.domain.friend.service.FriendService;
import kuchat.server.domain.jwt.JwtTokenService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.exception.BaseResponse.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("friend")
@RestController
public class FriendController {

    private final JwtTokenService jwtTokenService;
    private final FriendService friendService;

    @Operation(summary = "친구 프로필에서 친구 신청 보내기")
    @PostMapping("/apply")
    public ResponseEntity<BaseResponse> applyFriend(@AuthenticationPrincipal Member member,
                                                    @RequestBody FriendRequest request){
        log.info("[applyFriend] 친구 신청 요청 = {}", request.toString());
        friendService.apply(member, request);
        return ResponseEntity.ok(FRIEND_APPLY_SUCCESS);
    }

    @Operation(summary = "plus id로 친구 신청 보내기")
    @PostMapping("/apply/{plusId}")
    public ResponseEntity<BaseResponse> applyFriendByPlusId(@PathVariable("plusId") String plusId,
                                                            @AuthenticationPrincipal Member member){
        log.info("[applyFriendByPlusId] plusId = {} 인 친구에서 {} 가 친구 요청을 보냄", plusId, member.getId());
        friendService.applyByPlusId(member, plusId);
        return ResponseEntity.ok(FRIEND_APPLY_SUCCESS);
    }

    @Operation(summary = "내가 보낸 친구 신청 목록 조회")
    @GetMapping("/sent-request")
    public ResponseEntity<ApplyResponses> getSentApply(@AuthenticationPrincipal Member member){
        log.info("[getSentApply] {} 가 보낸 친구 신청 목록 조회", member.getId());
        ApplyResponses response = friendService.getSentApply(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 받은 친구 신청 목록 조회")
    @GetMapping("/received-request")
    public ResponseEntity<ApplyResponses> getReceivedApply(@AuthenticationPrincipal Member member) {
        log.info("[getReceivedApply] {} 가 받은 친구 신청 목록 조회", member.getId());
        ApplyResponses response = friendService.getReceivedApply(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 신청 수락")
    @PatchMapping("/accept/{id}")
    public ResponseEntity<BaseResponse> acceptFriend(@AuthenticationPrincipal Member member,
                                                     @RequestParam("id") Long friendId){
        log.info("[acceptFriend] {}번 친구의 친구 신청 수락", friendId);
        friendService.accept(member, friendId);
        return ResponseEntity.ok(FRIEND_ACCEPT_SUCCESS);
    }

    @Operation(summary = "친구 신청 거절 (삭제)")
    @DeleteMapping("refuse/{id}")
    public ResponseEntity<BaseResponse> refuseApply(@AuthenticationPrincipal Member member,
                                                    @RequestParam("id") Long friendId){
        log.info("[refuseApply] {}번 친구의 친구 신청 거절 ", friendId);
        friendService.refuse(member, friendId);
        return ResponseEntity.ok(FRIEND_APPLY_REFUSE_SUCCESS);
    }

}
