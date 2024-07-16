package kuchat.server.domain.friend.service;

import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.friend.dto.FriendRequest;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.friend.repository.FriendRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static kuchat.server.common.exception.BaseResponse.*;
import static kuchat.server.domain.enums.FriendType.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public void apply(Member member, FriendRequest request) {
        log.info("[apply] sender id = {}, receiver id = {}", member.getId(), request.getReceiverId());
        Long senderId = request.getSenderId();
        if (senderId != member.getId()) {
            throw new KuchatException(BaseResponse.FRIEND_SENDER_UNMATCH);
        }
        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new KuchatException(BaseResponse.NOT_FOUND_MEMBER));
        Friend friend = new Friend(member, receiver, PENDING);
        Friend savedFriend = friendRepository.save(friend);
        updateFriendship(member, receiver, savedFriend);
    }

    @Transactional
    public void applyByPlusId(Member member, String plusId) {
        log.info("[applyByPlusId] sender id = {}, receiver plus id = {}", member.getId(), plusId);
        Member receiver = memberRepository.findByPlusId(plusId)
                .orElseThrow(() -> new KuchatException(BaseResponse.NOT_FOUND_PLUSID));
        Friend friend = new Friend(member, receiver, PENDING);
        Friend savedFriend = friendRepository.save(friend);
        updateFriendship(member, receiver, savedFriend);
    }

    public FriendResponses getSentApply(Member member) {
        log.info("[getSentApply] {} 가 보낸 친구 신청 목록 조회", member.getId());
        List<Friend> sentApply = friendRepository.findAllBySenderId(member.getId());        // 내가 보낸 친구신청 조회
        if (sentApply.isEmpty()) {
            return new FriendResponses(Collections.emptyList(), FRIEND_APPLY_LOOKUP_SUCCESS);
        }
        List<FriendResponse> responses = sentApply.stream()
                .map(friend -> memberRepository.findById(friend.getReceiver().getId())
                        .map(receiver -> new FriendResponse(receiver, friend.getCreatedDate()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return new FriendResponses(responses);
    }


    public FriendResponses getReceivedApply(Member member) {
        log.info("[getReceivedApply] {} 가 받은 친구 신청 목록 조회", member.getId());
        List<Friend> receiverApply = friendRepository.findAllByReceiverId(member.getId());        // 내가 받은 친구신청 조회
        if (receiverApply.isEmpty()) {
            return new FriendResponses(Collections.emptyList(), FRIEND_APPLY_LOOKUP_SUCCESS);
        }
        List<FriendResponse> responses = receiverApply.stream()
                .map(friend -> memberRepository.findById(friend.getSender().getId())
                        .map(sender -> new FriendResponse(sender, friend.getCreatedDate()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return new FriendResponses(responses);
    }

    @Transactional
    public void accept(Member member, Long senderId) {
        log.info("[accept] senderId={} 가 보낸 친구 신청을 memberId = {} 가 수락함", senderId, member.getId());
        Friend friend = friendRepository.findBySenderIdAndReceiverId(senderId, member.getId())
                .orElseThrow(() -> new KuchatException(NOT_FOUND_APPLY));
        friend.accept();
        Member sender = friend.getSender();
        updateFriendship(sender, member, friend);
    }

    @Transactional
    public void refuse(Member member, Long senderId) {
        log.info("[refuse] senderId={} 가 보낸 친구 신청을 memberId = {} 가 거절함", senderId, member.getId());
        Friend friend = friendRepository.findBySenderIdAndReceiverId(senderId, member.getId())
                .orElseThrow(() -> new KuchatException(NOT_FOUND_APPLY));
        friendRepository.delete(friend);
    }

    private void updateFriendship(Member sender, Member receiver, Friend friend) {
        receiver.addFriend(friend).isPresent(removedFriend -> friendRepository.delete(removedFriend));
        sender.addFriend(friend);

    }

    public FriendResponses getFriendList(Member member, String friendName) {
        log.info("[getFriendList] 이름에 '{}' 을 포함하는 친구 조회", friendName);
        List<Friend> friendships = friendRepository.findAllByName(member.getId(), friendName);
        List<FriendResponse> friendResponse = friendships.stream()
                .map(friend -> friend.getSender().equals(member) ? friend.getReceiver() : friend.getSender())
                .map(FriendResponse::new)
                .toList();
        return new FriendResponses(friendResponse);
    }

    public FriendResponse getFriendProfile(Member member, Long friendId) {
        log.info("[getFriendProfile] id가 {} 인 친구의 프로필 조회", friendId);
        if(friendRepository.findBlockedFriend(member.getId(), friendId).isPresent()){
            throw new KuchatException(BLOCKED_MEMBER);
        }
        Member friend = memberRepository.findById(friendId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        return new FriendResponse(friend);
    }

    public void block(Member member, Long friendId) {
        log.info("[block] {} 번 사용자가 {} 번 사용자를 차단함.", member.getId(), friendId);
        new Friend();
    }
}
