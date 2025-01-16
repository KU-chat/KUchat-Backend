package kuchat.server.domain.member.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.AuthToken;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.common.redis.RedisService;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.chatroom.service.ChatroomService;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.DetailProfileResponse;
import kuchat.server.domain.member.dto.ProfileUpdateRequest;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.dto.SignupResponse;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final RedisService redisService;
    private final ChatroomService chatroomService;

    @Transactional
    public ResponseEntity<BaseResponse> signup(Member member, SignupRequest signupRequest) {
        log.info("[signup] member = {}", member.toString());

        validateStudentId(signupRequest.getStudentId());
        member.updateInfo(signupRequest);
        memberRepository.findById(member.getId());

        // 엑세스 토큰, 리프레시 토큰 발급
        AuthToken authToken = jwtTokenService.generateAuthToken(member.getRole(), member.getId());
        log.info("[signup] member id : " + member.getId());

        SignupResponse response = new SignupResponse(
                SUCCESS,
                member.getId(),
                authToken.getAccessToken(),
                authToken.getRefreshToken()
        );

        return ResponseEntity.ok(response);
    }

    private void validateStudentId(String studentId) {
        memberRepository.findByStudentId(studentId).ifPresent(member -> {
            log.error("[error] 이미 존재하는 학번입니다.");
            throw new KuchatException(DUPLICATED_STUDENT_ID);
        });
    }

    public DetailProfileResponse getProfile(Member member) {
        return new DetailProfileResponse(member);
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

    @Transactional
    public void logout(Member member) {
        redisService.removeRefreshToken(member.getId());
    }

    @Transactional
    public BaseResponseStatus quit(Member member) {
        redisService.removeRefreshToken(member.getId());      // 로그아웃 처리
        memberRepository.delete(member);                      // 탈퇴 처리
        return SUCCESS;
    }

}
