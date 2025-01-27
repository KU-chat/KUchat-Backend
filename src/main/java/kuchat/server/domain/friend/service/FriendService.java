package kuchat.server.domain.friend.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.block.service.BlockService;
import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.friend.dto.FriendApplyResponses;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.repository.FriendRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final BlockService blockService;


    @Transactional
    public ResponseEntity<BaseResponse> applyByPlusId(Member member, String plusId) {
        log.info("[applyByPlusId] member id = {} 인 사용자가 plus id = {} 인 사용자를 친구로 추가함.", member.getId(), plusId);
        Member friendMember = getMemberByPlusId(plusId);
        validateAlreadyFriend(member.getId(), friendMember.getId());
        validateBlock(member.getId(), friendMember.getId());

        Friend newFriend = Friend.builder()
                .sender(member)
                .receiver(friendMember)
                .build();
        friendRepository.save(newFriend);
        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

    @Transactional
    public ResponseEntity<BaseResponse> acceptApply(Long memberId, Long friendId) {
        log.info("[acceptApply] friendId = {} 인 친구신청을 수신자인 {} 가 수락함", friendId, memberId);
        friendRepository.findByIdAndReceiver_IdAndAcceptance(friendId, memberId, false)
                .ifPresentOrElse(
                        Friend::accept,
                        () -> {
                            throw new KuchatException(NOT_FOUND_APPLY);
                        }
                );
        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

    private void validateBlock(Long memberId, Long friendMemberId) {
        if (blockService.isBlockOrBlocked(memberId, friendMemberId)) {
            throw new KuchatException(BLOCKED_MEMBER_APPLY);
        }
    }

    private void validateAlreadyFriend(Long memberId, Long friendMemberId) {
        if (friendRepository.findByMembers(memberId, friendMemberId).isPresent()) {
            throw new KuchatException(ALREADY_FRIEND);
        }
    }

    public ResponseEntity<BaseResponse> getFriendList(Member member, String friendName) {
        log.info("[getFriendList] 이름에 '{}' 을 포함하는 친구 조회", friendName);
        List<Friend> friendships = friendRepository.findAllByName(member, friendName);
        List<FriendResponse> friendResponse = friendships.stream()
                .map(Friend::getReceiver)
                .map(FriendResponse::new)
                .toList();
        FriendResponses responses = new FriendResponses(SUCCESS, friendResponse);
        return ResponseEntity.ok(responses);
    }

    @Transactional
    public ResponseEntity<BaseResponse> delete(Member member, Long friendMemberId) {
        log.info("[deleteFriendByMembers] member={}, friend={} 인 친구 관계 삭제", member.getId(), friendMemberId);
        Optional<Friend> friend = friendRepository.findByMembers(member.getId(), friendMemberId);
        friend.ifPresentOrElse(
                friendRepository::delete,
                () -> {
                    throw new KuchatException(NOT_FOUND_MEMBER);
                }
        );
        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

    private void deleteFriendByMembers(Long memberId, Long friendMemberId) {
        log.info("[deleteFriendByMembers] member={}, friend={} 인 친구 관계 삭제", memberId, friendMemberId);
        Optional<Friend> friend = friendRepository.findByMembers(memberId, friendMemberId);
        friend.ifPresentOrElse(
                friendRepository::delete,
                () -> {
                    throw new KuchatException(NOT_FOUND_MEMBER);
                }
        );
    }

    private Member getMemberByPlusId(String plusId) {
        return memberRepository.findByPlusId(plusId)
                .orElseThrow(() -> new KuchatException(BaseResponseStatus.NOT_FOUND_PLUSID));
    }

    @Transactional
    public void deleteIfFriend(Long memberId, Long friendMemberId) {
        friendRepository.findByMembers(memberId, friendMemberId).
                ifPresent(friendRepository::delete);
    }

    public ResponseEntity<FriendApplyResponses> getFriendApplyList(Member member, Pageable pageable) {
        log.info("[getFriendApplyList] 친구신청 목록 조회");
        Page<Friend> friends = friendRepository.findAllByReceiverAndAcceptance(member, false, pageable);
        FriendApplyResponses responses = new FriendApplyResponses(SUCCESS, friends);
        return ResponseEntity.ok(responses);
    }
}
