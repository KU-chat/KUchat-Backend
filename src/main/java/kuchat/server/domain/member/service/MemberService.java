package kuchat.server.domain.member.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.ChatroomMember;
import kuchat.server.domain.enums.Platform;
import kuchat.server.common.jwt.JwtTokenService;
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

import static kuchat.server.common.exception.BaseResponse.DUPLICATED_PLUSID;
import static kuchat.server.common.exception.BaseResponse.NOT_FOUND_MEMBER;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        Platform platform = Platform.of(signupRequest.getPlatform());
        Member member = memberRepository.findByPlatformAndAttributeName(platform, signupRequest.getAttributeName())
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
        member.updateInfo(signupRequest);

        // 엑세스 토큰, 리프레시 토큰 발급
        String accessToken = jwtTokenService.generateSTUDENTAccessToken(member);
        String refreshToken = jwtTokenService.generateRefreshToken();
        member.updateRefreshToken(refreshToken);

        log.info("[signup] member id : " + member.getId());
        log.info("[signup] Signup request access token: " + accessToken);
        log.info("[signup] Signup request refresh token: " + refreshToken);
        return new SignupResponse(member.getId(), accessToken, refreshToken);
    }

    public boolean duplicateStudentId(String studentId) {
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
    public void updateProfile(Member member, ProfileUpdateRequest request) {
        validateAndUpdatePlusId(member, request.getPlusId());
        member.updateProfile(request);
    }

    private void validateAndUpdatePlusId(Member member, String plusId) {
        memberRepository.findAllByPlusIdWithLock(plusId)
                .stream().findAny()
                .ifPresentOrElse(
                        duplicatedMember -> { throw new KuchatException(DUPLICATED_PLUSID); },
                        () -> member.setPlusId(plusId)
                );
    }
}
