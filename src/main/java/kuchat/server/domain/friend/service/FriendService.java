package kuchat.server.domain.friend.service;

import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.friend.dto.ApplyResponse;
import kuchat.server.domain.friend.dto.ApplyResponses;
import kuchat.server.domain.friend.dto.FriendRequest;
import kuchat.server.domain.friend.repository.FriendRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static kuchat.server.common.exception.BaseResponse.FRIEND_APPLY_LOOKUP_SUCCESS;
import static kuchat.server.common.exception.BaseResponse.NOT_FOUND_APPLY;
import static kuchat.server.domain.enums.FriendType.PENDING;

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

    public ApplyResponses getSentApply(Member member) {
        log.info("[getSentApply] {} 가 보낸 친구 신청 목록 조회", member.getId());
        List<Friend> sentApply = friendRepository.findAllBySenderId(member.getId());        // 내가 보낸 친구신청 조회
        if (sentApply.isEmpty()) {
            return new ApplyResponses(Collections.emptyList(), FRIEND_APPLY_LOOKUP_SUCCESS);
        }
        List<ApplyResponse> responses = sentApply.stream()
                .map(friend -> memberRepository.findById(friend.getReceiver().getId())
                        .map(receiver -> new ApplyResponse(receiver, friend.getCreatedDate()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return new ApplyResponses(responses);
    }


    public ApplyResponses getReceivedApply(Member member) {
        log.info("[getReceivedApply] {} 가 받은 친구 신청 목록 조회", member.getId());
        List<Friend> receiverApply = friendRepository.findAllByReceiverId(member.getId());        // 내가 받은 친구신청 조회
        if (receiverApply.isEmpty()) {
            return new ApplyResponses(Collections.emptyList(), FRIEND_APPLY_LOOKUP_SUCCESS);
        }
        List<ApplyResponse> responses = receiverApply.stream()
                .map(friend -> memberRepository.findById(friend.getSender().getId())
                        .map(sender -> new ApplyResponse(sender, friend.getCreatedDate()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
        return new ApplyResponses(responses);
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
        receiver.addFriend(friend);
        sender.addFriend(friend);
    }

}
