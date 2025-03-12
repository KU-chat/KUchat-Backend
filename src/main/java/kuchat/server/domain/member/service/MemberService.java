package kuchat.server.domain.member.service;

import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.redis.RedisService;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.auth.dto.AuthTokenResponse;
import kuchat.server.domain.auth.dto.GuestTokenResponse;
import kuchat.server.domain.auth.service.JwtTokenService;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.repository.MemberRepository;
import kuchat.server.domain.oauth.dto.GoogleInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.*;
import static kuchat.server.domain.enums.Platform.GOOGLE;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final JwtTokenService jwtTokenService;

    @Value("${default.profile.address}")
    private String defaultImage;

    @Transactional
    public AuthTokenResponse signup(Member member, SignupRequest signupRequest, HttpServletResponse response) {
        log.info("[signup] member = {}", member.toString());
        validateStudentId(signupRequest.getStudentIdNumber());
        member.updateInfo(signupRequest, defaultImage);
        return handleStudent(member);
    }

    private void validateStudentId(String studentId) {
        log.info("[validateStudentId] 학번 = {}", studentId);
        memberRepository.findByStudentId(studentId).ifPresent(member -> {
            log.error("[error] 이미 존재하는 학번입니다.");
            throw new KuchatException(DUPLICATED_STUDENT_ID);
        });
    }

    @Transactional
    public void logout(Member member) {
        log.info("[logout] 로그아웃 요청! 사용자 id = {}, name = {}", member.getId(), member.getName());
        redisService.removeRefreshToken(member.getId());
    }

    @Transactional
    public void quit(Member member) {
        redisService.removeRefreshToken(member.getId());      // 로그아웃 처리
        memberRepository.delete(member);                      // 탈퇴 처리
    }

    public Member getMemberByPlusId(String plusId) {
        return memberRepository.findByPlusId(plusId)
                .orElseThrow(() -> new KuchatException(BaseResponseStatus.NOT_FOUND_PLUSID));
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    public List<Member> getMembersByIds(List<Long> friends) {
        return memberRepository.findAllById(friends);
    }

    @Transactional
    public Member lookupMemberByGoogleInfo(GoogleInfoResponse infoResponse) {
        return memberRepository.findByPlatformAndProviderId(GOOGLE, infoResponse.getId())
                .orElseGet(() -> {
                    Member member = new Member(infoResponse.getEmail(),
                            GOOGLE,
                            infoResponse.getId(),
                            infoResponse.getPicture());
                    memberRepository.save(member);
                    return getMemberById(member.getId());
                });
    }

    public GuestTokenResponse handleGuest(Member member){
        String guestToken = jwtTokenService.generateGuestToken(GOOGLE.name(), member.getProviderId());
        return new GuestTokenResponse(SUCCESS, guestToken);
    }

    public AuthTokenResponse handleStudent(Member member){
        return jwtTokenService.generateAuthToken(member.getRole(), member.getId());
    }

}
