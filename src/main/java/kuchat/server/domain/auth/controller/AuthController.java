package kuchat.server.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.auth.service.JwtTokenService;
import kuchat.server.domain.auth.dto.AuthTokenResponse;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.service.MemberService;
import kuchat.server.domain.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kuchat.server.domain.enums.Role.STUDENT;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final JwtTokenService jwtTokenService;
    private final MemberService memberService;

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthTokenResponse> reissueToken(@RequestHeader(value = "Authorization", required = false) String refreshToken) {
        Long memberId = jwtTokenService.validateRefreshToken(refreshToken);
        AuthTokenResponse response = jwtTokenService.generateAuthToken(STUDENT, memberId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@RequestHeader("Authorization") String accessToken, HttpServletResponse response) {
        Member member = jwtTokenService.extractMemberByAccessToken(accessToken);
        memberService.logout(member);
        return CookieUtil.removeAuthToken();
    }
}
