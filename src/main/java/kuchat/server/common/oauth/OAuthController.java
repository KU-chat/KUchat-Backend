package kuchat.server.common.oauth;

import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.domain.member.dto.AuthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/oauth")
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/google")
    public ResponseEntity<AuthTokenResponse> callback(@RequestParam("code") String code,
                                                      HttpServletResponse response){
        log.info("[callback] 구글 인가코드 발급 완료 = {}", code);
        AuthTokenResponse authTokenResponse = oAuthService.process(code, response);
        return ResponseEntity.ok(authTokenResponse);
    }
}
