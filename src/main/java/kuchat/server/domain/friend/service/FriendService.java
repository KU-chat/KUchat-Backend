package kuchat.server.domain.friend.service;

import kuchat.server.common.exception.KuchatException;
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
import java.util.Set;

import static kuchat.server.common.exception.BaseResponse.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;


    public FriendResponses getFriendList(Member member, String friendName) {
        log.info("[getFriendList] 이름에 '{}' 을 포함하는 친구 조회", friendName);
        List<Friend> friendships = friendRepository.findAllByName(member, friendName);
        List<FriendResponse> friendResponse = friendships.stream()
                .map(friend -> friend.getMember1().equals(member) ? friend.getMember2() : friend.getMember1())
                .map(FriendResponse::new)
                .toList();
        return new FriendResponses(friendResponse);
    }

    public FriendResponse getFriendProfile(Member member, Long friendId) {
        log.info("[getFriendProfile] id가 {} 인 친구의 프로필 조회", friendId);
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        if (member.block(friend) || friend.block(member)) {
            throw new KuchatException(BLOCKED_MEMBER);
        }
        return new FriendResponse(friend);
    }

    @Transactional
    public boolean addFriend(Member sender, Member receiver) {
        friendRepository.findByMembers(sender, receiver)
                .ifPresentOrElse(
                        friend -> {
                            throw new KuchatException(ALREADY_FRIEND);
                        },
                        () -> {
                            Friend newFriend = new Friend(sender, receiver);
                            addFriend(sender, receiver, newFriend);
                        }
                );
        return true;
    }

    private void addFriend(Member sender, Member receiver, Friend newFriend) {
        Friend saved = friendRepository.save(newFriend);
        sender.addFriend(saved);
        receiver.addFriend(saved);
    }

    public void delete(Member member, Long id) {
        Member member2 = memberRepository.findById(id)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        friendRepository.findByMembers(member, member2)
                .ifPresentOrElse(
                        friend -> {
                            member.deleteFriend(friend);
                            member2.deleteFriend(friend);
                            friendRepository.delete(friend);
                        },
                        () -> {
                            throw new KuchatException(NOT_FOUND_FRIEND);
                        }
                );
    }
}
