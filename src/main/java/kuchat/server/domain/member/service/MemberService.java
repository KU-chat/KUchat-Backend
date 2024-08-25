package kuchat.server.domain.member.service;

import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.AuthToken;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.common.redis.RedisService;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.ChatroomMember;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.ProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.dto.SignupResponse;
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
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final RedisService redisService;

    @Transactional
    public SignupResponse signup(String guestToken, SignupRequest signupRequest) {
        log.info("[signup] guestToken = {}", guestToken);
        Member member = jwtTokenService.extractMemberByGuestToken(guestToken);
        log.info("[signup] member = {}", member.toString());
        if (duplicateStudentId(signupRequest.getStudentId())) {
            log.error("[error] 이미 존재하는 학번입니다.");
            throw new KuchatException(DUPLICATED_STUDENT_ID);
        }
        member.updateInfo(signupRequest);

        // 엑세스 토큰, 리프레시 토큰 발급
        AuthToken authToken = jwtTokenService.generateAuthToken(member.getRole(), member.getId());

        log.info("[signup] member id : " + member.getId());
        log.info("[signup] Signup request access token: " + authToken.getAccessToken());
        log.info("[signup] Signup request refresh token: " + authToken.getRefreshToken());
        return new SignupResponse(member.getId(), authToken.getAccessToken(), authToken.getRefreshToken());
    }

    private boolean duplicateStudentId(String studentId) {
        return !memberRepository.findAllByStudentId(studentId)
                .isEmpty();
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    public List<Member> findMembersByChatroomId(Chatroom chatroom) {
        Set<ChatroomMember> chatroomMembers = chatroom.getChatroomMembers();
        return chatroomMembers.stream()
                .map(ChatroomMember::getMember)
                .toList();
    }

    public ProfileResponse getProfile(Member member) {
        return new ProfileResponse(member);
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
                            throw new KuchatException(DUPLICATED_PLUSID);
                        },
                        () -> member.setPlusId(plusId)
                );
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    public void logout(Member member) {
        redisService.removeRefreshToken(member.getId());
    }

//    public BaseResponse quit(Long memberId) {
//
//    }

}
