package kuchat.server.domain.block.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.block.Block;
import kuchat.server.domain.block.dto.BlockMemberResponses;
import kuchat.server.domain.block.repository.BlockRepository;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kuchat.server.common.response.BaseResponseStatus.*;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BlockService {

    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ResponseEntity<BaseResponse> block(Member member, Long blockMemberId) {
        log.info("[block] {} 번 사용자가 {} 번 사용자를 차단함.", member.getId(), blockMemberId);
        validateAlreadyBlock(member, blockMemberId);
        Member blocked = getMember(blockMemberId);
        Block block = new Block(member, blocked);
        blockRepository.save(block);
        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

    private void validateAlreadyBlock(Member member, Long blockMemberId) {
        // 이미 차단한 관계인지 확인하기
        if (blockRepository.findByIds(member.getId(), blockMemberId).isPresent()) {
            throw new KuchatException(ALREADY_BLOCK);
        }
    }

    @Transactional
    public ResponseEntity<BaseResponse> release(Member member, Long releaseMemberId) {
        log.info("[release] {} 번 사용자가 차단한 {} 번 사용자를 차단 해제함.", member.getId(), releaseMemberId);
        Member released = getMember(releaseMemberId);
        blockRepository.findByBlockerAndBlocked(member, released)
                .ifPresentOrElse(
                        blockRepository::delete,
                        () -> {
                            throw new KuchatException(NOT_FOUND_BLOCK);
                        }
                );
        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

    public ResponseEntity<BlockMemberResponses> getblocks(Member member, Pageable pageable) {
        log.info("[block] {} 번 사용자가 차단한 사용자 목록 조회", member.getId());
        Page<Block> blocks = blockRepository.findByBlocker(member, pageable);
        BlockMemberResponses response = new BlockMemberResponses(SUCCESS, blocks);
        return ResponseEntity.ok(response);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    public boolean isBlockOrBlocked(Long member1Id, Long member2Id) {
        Optional<Block> optionalBlock = blockRepository.findByIds(member1Id, member2Id);
        return optionalBlock.isPresent();
    }
}
