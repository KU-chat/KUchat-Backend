package kuchat.server.common.oauth;

import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.domain.member.dto.AuthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/oauth")
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/google")
    public ResponseEntity<AuthTokenResponse> callback(@RequestBody AuthRequest authRequest,
                                                      HttpServletResponse response){
        log.info("[callback] 구글 인가코드 발급 완료 = {}", authRequest.getCode());
        AuthTokenResponse authTokenResponse = oAuthService.process(authRequest.getCode(), response);
        return ResponseEntity.ok(authTokenResponse);
    }
}
