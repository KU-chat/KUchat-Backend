package kuchat.server.domain.relation.apply.service;

import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.friend.dto.FriendRequest;
import kuchat.server.domain.friend.dto.FriendResponse;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.friend.service.FriendService;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import kuchat.server.domain.relation.apply.Apply;
import kuchat.server.domain.relation.apply.repository.ApplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static kuchat.server.common.exception.BaseResponse.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ApplyService {

    private final MemberRepository memberRepository;
    private final ApplyRepository applyRepository;
    private final FriendService friendService;

    @Transactional
    public void send(Member member, FriendRequest request) {
        log.info("[apply] sender id = {}, receiver id = {}", member.getId(), request.getReceiverId());
        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new KuchatException(BaseResponse.NOT_FOUND_MEMBER));
        validateApply(member, receiver);
        Apply apply = new Apply(member, receiver);
        applyRepository.save(apply);
    }

    @Transactional
    public void applyByPlusId(Member sender, String plusId) {
        log.info("[applyByPlusId] sender id = {}, receiver plus id = {}", sender.getId(), plusId);
        Member receiver = memberRepository.findByPlusId(plusId)
                .orElseThrow(() -> new KuchatException(BaseResponse.NOT_FOUND_PLUSID));
        validateApply(sender, receiver);
        Apply apply = new Apply(sender, receiver);
        applyRepository.save(apply);
    }

    private void validateApply(Member sender, Member receiver) {
        if (applyRepository.findBySenderAndReceiver(sender, receiver).isPresent() ||
                applyRepository.findBySenderAndReceiver(receiver, sender).isPresent()) {
            throw new KuchatException(ALREADY_APPLY);
        }
    }

    public FriendResponses getSentApply(Member sender) {
        log.info("[getSentApply] {} 가 보낸 친구 신청 목록 조회", sender.getId());
//        List<Apply> sentApply = applyRepository.findAllBySender(sender);        // 내가 보낸 친구신청 조회
        Set<Apply> sentApply = sender.getSentApplies();        // 내가 보낸 친구신청 조회
        if (sentApply.isEmpty()) {
            return new FriendResponses(Collections.emptyList(), FRIEND_APPLY_LOOKUP_SUCCESS);
        }
        List<FriendResponse> responses = sentApply.stream()
                .map(apply -> new FriendResponse(apply.getReceiver(), apply.getCreatedDate()))
                .toList();
        return new FriendResponses(responses);
    }


    public FriendResponses getReceivedApply(Member receiver) {
        log.info("[getReceivedApply] {} 가 받은 친구 신청 목록 조회", receiver.getId());
//        List<Apply> receiverApply = applyRepository.findAllByReceiver(receiver);        // 내가 받은 친구신청 조회
        Set<Apply> receiverApply = receiver.getReceivedApplies();        // 내가 받은 친구신청 조회
        if (receiverApply.isEmpty()) {
            return new FriendResponses(Collections.emptyList(), FRIEND_APPLY_LOOKUP_SUCCESS);
        }
        List<FriendResponse> responses = receiverApply.stream()
                .map(apply -> new FriendResponse(apply.getSender(), apply.getCreatedDate()))
                .toList();
        return new FriendResponses(responses);
    }

    @Transactional
    public void accept(Member receiver, Long senderId) {
        log.info("[accept] senderId={} 가 보낸 친구 신청을 memberId = {} 가 수락함", senderId, receiver.getId());
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        Apply apply = applyRepository.findBySenderAndReceiver(sender, receiver)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_APPLY));
        if (friendService.addFriend(sender, receiver)) {
            deleteApply(receiver, sender, apply);
        }
    }

    @Transactional
    public void refuse(Member receiver, Long senderId) {
        log.info("[refuse] senderId={} 가 보낸 친구 신청을 memberId = {} 가 거절함", senderId, receiver.getId());
        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        Apply apply = applyRepository.findBySenderAndReceiver(sender, receiver)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_APPLY));
        deleteApply(receiver, sender, apply);
    }

    private void deleteApply(Member receiver, Member sender, Apply apply) {
        sender.deleteSentApply(apply);
        receiver.deleteReceivedApply(apply);
        applyRepository.delete(apply);
    }

}
