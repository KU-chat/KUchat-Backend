package kuchat.server.domain.friend.service;

import kuchat.server.common.exception.KuchatException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.BLOCKED_MEMBER;
import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_MEMBER;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;
    private final BlockService blockService;

    @Transactional
    public void addFriend(Member member, Long friendId) {
        log.info("[addFriend] member = {} 가 receiver id = {} 를 친구로 추가함", member.getId(), friendId);
        Member friendMember = memberRepository.findById(friendId)
                .orElseThrow(() -> new KuchatException(BaseResponseStatus.NOT_FOUND_MEMBER));
        Friend newFriend = Friend.builder()
                .follower(member)
                .followed(friendMember).build();
        friendRepository.save(newFriend);
    }

    @Transactional
    public void addByPlusId(Member member, String plusId) {
        log.info("[addByPlusId] member id = {} 인 사용자가 plus id = {} 인 사용자를 친구로 추가함.", member.getId(), plusId);
        Member friendMember = memberRepository.findByPlusId(plusId)
                .orElseThrow(() -> new KuchatException(BaseResponseStatus.NOT_FOUND_PLUSID));
        Friend newFriend = Friend.builder()
                .follower(member)
                .followed(friendMember).build();
        friendRepository.save(newFriend);
    }

    public FriendResponses getFriendList(Member member, String friendName) {
        log.info("[getFriendList] 이름에 '{}' 을 포함하는 친구 조회", friendName);
        List<Friend> friendships = friendRepository.findAllByName(member, friendName);
        List<FriendResponse> friendResponse = friendships.stream()
                .map(Friend::getFollowed)
                .map(FriendResponse::new)
                .toList();
        return new FriendResponses(friendResponse);
    }

    public FriendResponse getFriendProfile(Member member, Long friendId) {
        log.info("[getFriendProfile] id가 {} 인 친구의 프로필 조회", friendId);
        Member friendMember = getMember(friendId);
        // friendId인 멤버를 1. 내가 차단했거나, 2. 상대가 나를 차단한 경우
        // 예외가 발생해야 한다.

        if (blockService.isBlockOrBlocked(member, friendMember)) {
            throw new KuchatException(BLOCKED_MEMBER);
        }
        return new FriendResponse(friendMember);
    }

    public void delete(Member member, Long friendId) {
        Member friendMember = getMember(friendId);
        Friend friend = friendRepository.findByMembers(member, friendMember)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        friendRepository.delete(friend);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }
}
