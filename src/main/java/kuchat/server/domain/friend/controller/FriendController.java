package kuchat.server.domain.friend.controller;

import io.swagger.v3.oas.annotations.Operation;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.auth.argumentResolver.Auth;
import kuchat.server.domain.friend.dto.FriendApplyResponses;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.service.FriendService;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.utils.ValidatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PutMapping("/accept/{friendId}")
    public ResponseEntity<BaseResponse> acceptFriendApply(@Auth Member member,
                                                          @PathVariable("friendId") Long friendId) {
        log.info("[acceptFriendApply] friendId = {} 인 친구신청을 {} 가 수락함", friendId, member.getId());
        return friendService.acceptApply(member.getId(), friendId);
    }

    @Operation(summary = "내가 받은 친구신청 목록 조회")
    @GetMapping("/apply")
    public ResponseEntity<FriendApplyResponses> getFriendApplyList(@Auth Member member,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "20") int size) {
        log.info("[getFriendApplyList] id={} , name={} 가 받은 친구 신청 목록 조회", member.getId(), member.getName());
        Pageable pageable = PageRequest.of(page, ValidatorUtil.sizeValidator(size), Sort.Direction.DESC, "createdDate");
        return friendService.getFriendApplyList(member, pageable);
    }

    @Operation(summary = "친구신청 삭제")
    @DeleteMapping("/apply/{memberId}")
    public ResponseEntity<BaseResponse> deleteFriendApply(@Auth Member member,
                                                          @PathVariable("memberId") Long friendMemberId) {
        log.info("[deleteFriend] member id = {} 인 사용자가 보낸 친구신청 삭제", friendMemberId);
        return friendService.delete(member, friendMemberId, false);
    }

    @Operation(summary = "친구 삭제")
    @DeleteMapping("/{memberId}")
    public ResponseEntity<BaseResponse> deleteFriend(@Auth Member member,
                                                     @PathVariable("memberId") Long friendMemberId) {
        log.info("[deleteFriend] member id = {} 인 사용자와의 친구 관계 삭제", friendMemberId);
        return friendService.breakFriendship(member, friendMemberId);
    }

    @Operation(summary = "친구 목록 조회 (이름 검색)")
    @GetMapping
    public ResponseEntity<FriendResponses> getFriendList(@Auth Member member,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size,
                                                         @RequestParam(value = "name", required = false) String friendName) {
        log.info("[getFriendList] 친구 목록 검색 및 조회. 검색 문자열 = '{}'", friendName);
        Pageable pageable = PageRequest.of(page,  ValidatorUtil.sizeValidator(size), Sort.Direction.ASC, "receiver.profile.name");
        return friendService.getFriendList(member, friendName, pageable);
    }
}
