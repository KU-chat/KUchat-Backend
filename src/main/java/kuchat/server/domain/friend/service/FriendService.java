package kuchat.server.domain.friend.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.block.service.BlockService;
import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.repository.FriendRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("[addByPlusId] member id = {} 인 사용자가 plus id = {} 인 사용자를 친구로 추가함.", member.getId(), plusId);
        Member friendMember = getMemberByPlusId(plusId);
        validateAlreadyFriend(member, friendMember);
        validateBlock(member, friendMember);

        Friend newFriend = Friend.builder()
                .sender(member)
                .receiver(friendMember)
                .build();
        friendRepository.save(newFriend);
        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

    private void validateBlock(Member member, Member friendMember) {
        if (blockService.isBlockOrBlocked(member.getId(), friendMember.getId())){
            throw new KuchatException(BLOCKED_MEMBER_APPLY);
        }
    }

    private void validateAlreadyFriend(Member member, Member friendMember) {
        if (friendRepository.findByMembers(member, friendMember).isPresent()){
            throw new KuchatException(ALREADY_FRIEND);
        }
    }

    public FriendResponses getFriendList(Member member, String friendName) {
        log.info("[getFriendList] 이름에 '{}' 을 포함하는 친구 조회", friendName);
        List<Friend> friendships = friendRepository.findAllByName(member, friendName);
        List<FriendResponse> friendResponse = friendships.stream()
                .map(Friend::getReceiver)
                .map(FriendResponse::new)
                .toList();
        return new FriendResponses(friendResponse);
    }

    public void delete(Member member, Long friendId) {
        Member friendMember = getMember(friendId);
        Optional<Friend> friend = friendRepository.findByMembers(member, friendMember);
        friend.ifPresentOrElse(
                friendRepository::delete,
                () -> {
                    throw new KuchatException(NOT_FOUND_MEMBER);
                }
        );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    private Member getMemberByPlusId(String plusId) {
        return memberRepository.findByPlusId(plusId)
                .orElseThrow(() -> new KuchatException(BaseResponseStatus.NOT_FOUND_PLUSID));
    }
}
