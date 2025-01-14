package kuchat.server.common.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.KuchatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtTokenService jwtTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // true 반환 시 인터셉터 통과, 컨트롤러로!
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessToken == null) {
            log.info("[validateToken] token is null");
            throw new KuchatException(NOT_FOUND_TOKEN);
        }
        accessToken = accessToken.replaceAll("Bearer ", "");
        log.info("[preHandle] access token = {}", accessToken);

        if (request.getRequestURI().contains("/member/signup") ||
                request.getRequestURI().equals("/") ||
                request.getRequestURI().equals("/login")) {
            log.info("[preHandle] request uri = {} 는 인증절차 필요X", accessToken);
            return true;
        }

        Long memberId = jwtTokenService.getMemberId(accessToken);
        log.info("[preHandle] member id by token = {}", memberId);
        request.setAttribute("memberId", memberId);

        String role = jwtTokenService.getClaims(accessToken).get("role", String.class);
        log.info("[preHandle] role by token = {}", role);
        request.setAttribute("role", role);

        return true;
    }

}
