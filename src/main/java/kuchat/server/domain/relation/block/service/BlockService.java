package kuchat.server.domain.relation.block.service;

import kuchat.server.domain.friend.Friend;
import kuchat.server.domain.friend.dto.FriendResponses;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlockService {

    public void block(Member member, Long friendId) {
        log.info("[block] {} 번 사용자가 {} 번 사용자를 차단함.", member.getId(), friendId);
        new Friend();
    }

    public void release(Member member, Long blockMemberId) {

    }

    public FriendResponses getblocks(Member member) {
        return null;
    }
}
