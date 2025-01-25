package kuchat.server.domain.block.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.block.Block;
import kuchat.server.domain.block.dto.BlockMemberResponse;
import kuchat.server.domain.block.dto.BlockMemberResponses;
import kuchat.server.domain.block.repository.BlockRepository;
import kuchat.server.domain.friend.repository.FriendRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_BLOCK;
import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_MEMBER;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    public void block(Member member, Long blockMemberId) {
        log.info("[block] {} 번 사용자가 {} 번 사용자를 차단함.", member.getId(), blockMemberId);
        Member blocked = getMember(blockMemberId);

        deleteFriend(member, blocked);          // member 가 blocked 를 팔로우한 경우, 제거
        deleteFriend(blocked, member);          // blocked 가 member 를 팔로우한 경우, 제거

        Block block = new Block(member, blocked);
        blockRepository.save(block);
    }

    // m1 이 m2를 팔로우한 경우, 그 때 생성된 friend 를 제거함
    private void deleteFriend(Member m1, Member m2) {
        friendRepository.findByMembers(m1, m2)
                .ifPresent(friendRepository::delete);
    }

    public void release(Member member, Long releaseMemberId) {
        log.info("[release] {} 번 사용자가 차단한 {} 번 사용자를 차단 해제함.", member.getId(), releaseMemberId);
        Member released = getMember(releaseMemberId);
        Block block = blockRepository.findByBlockerAndBlocked(member, released)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_BLOCK));
        blockRepository.delete(block);
    }

    public BlockMemberResponses getblocks(Member member) {
        log.info("[block] {} 번 사용자가 차단한 사용자 목록 조회", member.getId());
        List<BlockMemberResponse> responses = blockRepository.findByBlocker(member)
                .stream()
                .map(Block::getBlocked)
                .map(BlockMemberResponse::new)
                .toList();
        return new BlockMemberResponses(responses);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    public boolean isBlockOrBlocked(Long member1Id, Long member2Id){
        Optional<Block> optionalBlock = blockRepository.findByIds(member1Id, member2Id);
        return optionalBlock.isPresent();
    }
}
