package kuchat.server.domain.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.jwt.argumentResolver.Auth;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.service.FriendService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/friend")
@RestController
public class FriendController {

    private final FriendService friendService;

    @Operation(summary = "plus id로 친구 신청 보내기")
    @PostMapping("/apply/{plusId}")
    public ResponseEntity<BaseResponse> sendApplyByPlusId(@Auth Member member,
                                                          @PathVariable("plusId") String plusId) {
        log.info("[applyFriendByPlusId] plusId = {} 인 친구에서 {} 가 친구 요청을 보냄", plusId, member.getId());
        return friendService.applyByPlusId(member, plusId);
    }

    @Operation(summary = "친구신청 수락하기")
    @PostMapping("/accept/{friendId}")
    public ResponseEntity<BaseResponse> acceptFriendApply(@Auth Member member,
                                                    @PathVariable("friendId") Long friendId) {
        log.info("[acceptFriendApply] friendId = {} 인 친구신청을 {} 가 수락함", friendId, member.getId());
        return friendService.acceptApply(member.getId(), friendId);
    }

    @Operation(summary = "친구 목록 조회 (이름 검색)")
    @GetMapping("")
    public ResponseEntity<BaseResponse> getFriendList(@Auth Member member,
                                                         @RequestParam("name") String friendName) {
        log.info("[getFriendList] 친구 목록 검색 및 조회. 검색 문자열 = '{}'", friendName);
        return friendService.getFriendList(member, friendName);
    }

    @Operation(summary = "친구 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteFriend(@Auth Member member,
                                                           @PathVariable("id") Long friendId) {
        log.info("[deleteFriend] member id = {} 인 사용자와의 친구 관계 삭제", friendId);
        return friendService.delete(member, friendId);
    }

}
