package kuchat.server.domain.member.service;

import kuchat.server.domain.jwt.JwtTokenUtil;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.SignupRequest;
import kuchat.server.domain.member.dto.SignupResponse;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public SignupResponse signup(SignupRequest signupRequest) {
        Member member = memberRepository.save(new Member(signupRequest));
        // 엑세스 토큰, 리프레시 토큰 발급
        String accessToken = JwtTokenUtil.generateToken(member.getId());
        String refreshToken = JwtTokenUtil.generateToken(member.getId());

        return new SignupResponse(member.getId(), accessToken, refreshToken);
    }
}
