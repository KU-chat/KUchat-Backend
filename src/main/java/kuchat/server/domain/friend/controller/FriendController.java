package kuchat.server.domain.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.friend.dto.FriendRequest;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.friend.service.FriendService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.exception.BaseResponse.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/friend")
@RestController
public class FriendController {

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
    public ResponseEntity<FriendResponses> getSentApply(@AuthenticationPrincipal Member member){
        log.info("[getSentApply] {} 가 보낸 친구 신청 목록 조회", member.getId());
        FriendResponses response = friendService.getSentApply(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 받은 친구 신청 목록 조회")
    @GetMapping("/received-request")
    public ResponseEntity<FriendResponses> getReceivedApply(@AuthenticationPrincipal Member member) {
        log.info("[getReceivedApply] {} 가 받은 친구 신청 목록 조회", member.getId());
        FriendResponses response = friendService.getReceivedApply(member);
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
    @DeleteMapping("/refuse/{id}")
    public ResponseEntity<BaseResponse> refuseApply(@AuthenticationPrincipal Member member,
                                                    @RequestParam("id") Long friendId){
        log.info("[refuseApply] {}번 친구의 친구 신청 거절 ", friendId);
        friendService.refuse(member, friendId);
        return ResponseEntity.ok(FRIEND_APPLY_REFUSE_SUCCESS);
    }

    @Operation(summary = "친구 목록 조회 (이름 검색)")
    @GetMapping("")
    public ResponseEntity<FriendResponses> getFriendList(@AuthenticationPrincipal Member member,
                                                         @RequestParam("name") String friendName){
        log.info("[getFriendList] 친구 목록 검색 및 조회. 검색 문자열 = '{}'", friendName);
        FriendResponses response = friendService.getFriendList(member, friendName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "다른 사용자 프로필 조회 (차단했거나 차단당한 사용자의 프로필은 조회 불가능)")
    @GetMapping("/{id}/profile")
    public ResponseEntity<FriendResponse> getFriendProfile(@AuthenticationPrincipal Member member,
                                                           @PathVariable("id") Long friendId){
        log.info("[getFriendProfile] ");
        FriendResponse response = friendService.getFriendProfile(member, friendId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 차단하기")        // 친구가 아니었어도 차단 가능
    @PostMapping("/{id}/block")
    public ResponseEntity<BaseResponse> blockMember(@AuthenticationPrincipal Member member,
                                                    @RequestParam("id") Long friendId){
        log.info("[blockMember]");
        friendService.block(member, friendId);
        return ResponseEntity.ok(BLOCK_MEMBER_SUCCESS);
    }

    @Operation(summary = "사용자 차단 해제하기")
    @DeleteMapping("/{id}/block")
    public ResponseEntity<BaseResponse> unblockMember(){
        log.info("[unblockMember]");

        return ResponseEntity.ok(DELETE_BLOCK_SUCCESS);
    }

    @Operation(summary = "내가 차단한 사용자 목록 조회")
    @GetMapping("/block")
    public ResponseEntity<FriendResponses> blockMemberList(@AuthenticationPrincipal Member member){
        log.info("[blockMemberList]");


    }
}
