package kuchat.server.domain.member.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.DetailProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kuchat.server.common.response.BaseResponseStatus.DUPLICATED_PLUSID;
import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_MEMBER;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileService {
    private final MemberRepository memberRepository;

    public ResponseEntity<DetailProfileResponse> getProfile(Member member) {
        return ResponseEntity.ok(new DetailProfileResponse(member));
    }

    @Transactional
    public void updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = getMember(memberId);
        validateAndUpdatePlusId(memberId, request.getPlusId());
        member.updateProfile(request);
    }

    private void validateAndUpdatePlusId(Long memberId, String plusId) {
        Member member = getMember(memberId);
        memberRepository.findAllByPlusIdWithLock(plusId)
                .stream().findAny()
                .ifPresentOrElse(
                        duplicatedMember -> {
                            if(!duplicatedMember.equals(member)){
                                throw new KuchatException(DUPLICATED_PLUSID);
                            }
                        },
                        () -> member.setPlusId(plusId)
                );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }
}
