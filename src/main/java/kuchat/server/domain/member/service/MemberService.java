package kuchat.server.domain.member.service;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.AuthToken;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.common.redis.RedisService;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.dto.SignupResponse;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenService jwtTokenService;
    private final RedisService redisService;

    @Value("${default.profile.address}")
    private String defaultImage;

    @Transactional
    public ResponseEntity<BaseResponse> signup(Member member, SignupRequest signupRequest) {
        log.info("[signup] member = {}", member.toString());

        validateStudentId(signupRequest.getStudentIdNumber());
        member.updateInfo(signupRequest, defaultImage);
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
        log.info("[validateStudentId] 학번 = {}", studentId);
        memberRepository.findByStudentId(studentId).ifPresent(member -> {
            log.error("[error] 이미 존재하는 학번입니다.");
            throw new KuchatException(DUPLICATED_STUDENT_ID);
        });
    }

    @Transactional
    public void logout(Member member) {
        redisService.removeRefreshToken(member.getId());
    }

    @Transactional
    public ResponseEntity<BaseResponse> quit(Member member) {
        redisService.removeRefreshToken(member.getId());      // 로그아웃 처리
        memberRepository.delete(member);                      // 탈퇴 처리
        return ResponseEntity.ok(new BaseResponse(SUCCESS));
    }

    public Member getMemberByPlusId(String plusId) {
        return memberRepository.findByPlusId(plusId)
                .orElseThrow(() -> new KuchatException(BaseResponseStatus.NOT_FOUND_PLUSID));
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    public List<Member> getMembers(List<Long> friends) {
        return memberRepository.findAllById(friends);
    }
}
