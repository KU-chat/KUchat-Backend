package kuchat.server.domain.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.argumentResolver.Auth;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.exception.BaseResponse.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/friend")
@RestController
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "친구 프로필에서 친구 신청 보내기")
    @PostMapping("/{id}")
    public ResponseEntity<BaseResponse> addFriend(@Auth Long memberId,
                                                  @PathVariable("id") Long friendId) {
        log.info("[applyFriend] id = {} 인 사용자를 친구로 추가", friendId);
        friendService.addFriend(memberId, friendId);
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "plus id로 친구 신청 보내기")
    @PostMapping("plusId/{plusId}")
    public ResponseEntity<BaseResponse> sendApplyByPlusId(@Auth Long memberId,
                                                          @PathVariable("plusId") String plusId) {
        log.info("[applyFriendByPlusId] plusId = {} 인 친구에서 {} 가 친구 요청을 보냄", plusId, memberId);
        friendService.addByPlusId(memberId, plusId);
        return ResponseEntity.ok(SUCCESS);
    }

    @Operation(summary = "친구 목록 조회 (이름 검색)")
    @GetMapping("")
    public ResponseEntity<FriendResponses> getFriendList(@Auth Long memberId,
                                                         @RequestParam("name") String friendName) {
        log.info("[getFriendList] 친구 목록 검색 및 조회. 검색 문자열 = '{}'", friendName);
        FriendResponses response = friendService.getFriendList(memberId, friendName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "다른 사용자 프로필 조회 (차단했거나 차단당한 사용자의 프로필은 조회 불가능)")
    @GetMapping("/{id}/profile")
    public ResponseEntity<FriendResponse> getFriendProfile(@Auth Long memberId,
                                                           @PathVariable("id") Long friendId) {
        log.info("[getFriendProfile] id = {} 인 사용자의 프로필 조회 요청", friendId);
        FriendResponse response = friendService.getFriendProfile(memberId, friendId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "친구 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteFriend(@Auth Long memberId,
                                                     @PathVariable("id") Long friendId) {
        log.info("[deleteFriend] member id = {} 인 사용자와의 친구 관계 삭제", friendId);
        friendService.delete(memberId, friendId);
        return ResponseEntity.ok(SUCCESS);
    }

}
