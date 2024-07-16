package kuchat.server.domain.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.friend.dto.FriendRequest;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.service.FriendService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.exception.BaseResponse.FRIEND_APPLY_SUCCESS;
import static kuchat.server.common.exception.BaseResponse.FRIEND_DELETE_SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/friend")
@RestController
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 프로필에서 친구 신청 보내기")
    @PostMapping("")
    public ResponseEntity<BaseResponse> addFriend(@AuthenticationPrincipal Member member,
                                                  @RequestBody FriendRequest request) {
        log.info("[applyFriend] 친구 신청 요청 = {}", request.toString());
        friendService.addFriend(member, request);
        return ResponseEntity.ok(FRIEND_APPLY_SUCCESS);
    }

    @Operation(summary = "plus id로 친구 신청 보내기")
    @PostMapping("/{plusId}")
    public ResponseEntity<BaseResponse> sendApplyByPlusId(@PathVariable("plusId") String plusId,
                                                          @AuthenticationPrincipal Member member) {
        log.info("[applyFriendByPlusId] plusId = {} 인 친구에서 {} 가 친구 요청을 보냄", plusId, member.getId());
        friendService.addByPlusId(member, plusId);
        return ResponseEntity.ok(FRIEND_APPLY_SUCCESS);
    }

    @Operation(summary = "친구 목록 조회 (이름 검색)")
    @GetMapping("")
    public ResponseEntity<FriendResponses> getFriendList(@AuthenticationPrincipal Member member,
                                                         @RequestParam("name") String friendName) {
        log.info("[getFriendList] 친구 목록 검색 및 조회. 검색 문자열 = '{}'", friendName);
        FriendResponses response = friendService.getFriendList(member, friendName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "다른 사용자 프로필 조회 (차단했거나 차단당한 사용자의 프로필은 조회 불가능)")
    @GetMapping("/{id}/profile")
    public ResponseEntity<FriendResponse> getFriendProfile(@AuthenticationPrincipal Member member,
                                                           @PathVariable("id") Long id) {
        log.info("[getFriendProfile] id = {} 인 사용자의 프로필 조회 요청", id);
        FriendResponse response = friendService.getFriendProfile(member, id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 삭제")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<BaseResponse> deleteFriend(@AuthenticationPrincipal Member member,
                                                     @PathVariable("id") Long id) {
        log.info("[deleteFriend] member id = {} 인 사용자와의 친구 관계 삭제", id);
        friendService.delete(member, id);
        return ResponseEntity.ok(FRIEND_DELETE_SUCCESS);
    }

}
