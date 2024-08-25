package kuchat.server.common.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static kuchat.server.common.exception.BaseResponse.EXPIRED_TOKEN;
import static kuchat.server.common.exception.BaseResponse.INVALID_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final JwtTokenService jwtTokenService;
    private final RedisService redisService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> reissueToken(@CookieValue(value = "refresh-token", required = false) String refreshToken,
                                          HttpServletResponse response) throws IOException {
        log.info("[reissueToken] 리프레시 토큰 = {}", refreshToken);
        if (refreshToken == null || !jwtTokenService.validatedRefreshToken(refreshToken)) {
            // 클라이언트 쿠키에서 토큰 제거
            Cookie cookie = new Cookie("refresh-token", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
//            redisService.removeRefreshToken(member.getId());
            response.sendRedirect("/oauth2/authorization/google");
            return null;
        }
        AuthToken authToken = jwtTokenService.reissue(refreshToken);
        return ResponseEntity.ok(authToken);
    }
}
