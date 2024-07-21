package kuchat.server.common.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION).replaceAll("Bearer ", "");
        log.info("[preHandle] access token = {}", accessToken);

        if (request.getRequestURI().contains("/member/signup") ||
                request.getRequestURI().equals("/") ||
                request.getRequestURI().equals("/login")) {
            return true;
        }

        try {
            validateToken(accessToken);
        } catch (KuchatException e) {
            log.error("[doFilterInternal] error message = {}", e.getMessage());
            return false;
        }

        Long memberId = jwtTokenService.getMemberId(accessToken);
        log.info("[preHandle] member id by token = {}", memberId);
        request.setAttribute("memberId", memberId);

        String role = jwtTokenService.getClaims(accessToken).get("role", String.class);
        log.info("[preHandle] role by token = {}", role);
        request.setAttribute("role", role);

        return true;
    }

    private void validateToken(String token) {
        if (token == null) {
            throw new KuchatException(BaseResponse.NOT_FOUND_TOKEN);
        }
        if (!token.startsWith("Bearer ")) {
            throw new KuchatException(BaseResponse.INVALID_TOKEN);
        }
    }
}
