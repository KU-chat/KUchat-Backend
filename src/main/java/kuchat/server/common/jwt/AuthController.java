package kuchat.server.common.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kuchat.server.common.exception.BaseResponse.INVALID_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final JwtTokenService jwtTokenService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> reissueToken(@CookieValue(value = "refresh-token", required = false) String refreshToken) {
        log.info("[reissueToken] 리프레시 토큰 = {}", refreshToken);
        if (!jwtTokenService.validatedRefreshToken(refreshToken)) {
            return ResponseEntity.badRequest().body(INVALID_TOKEN);
        }
        AuthToken authToken = jwtTokenService.reissue(refreshToken);
        return ResponseEntity.ok(authToken);
    }
}
