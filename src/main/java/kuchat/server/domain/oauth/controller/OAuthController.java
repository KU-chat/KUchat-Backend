package kuchat.server.domain.oauth.controller;

import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.domain.oauth.dto.AuthorizationCodeRequest;
import kuchat.server.domain.oauth.service.GoogleOAuthService;
import kuchat.server.domain.member.dto.AuthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/oauth")
@RestController
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://kuchat.netlify.app", allowCredentials = "true")
public class OAuthController {

    private final GoogleOAuthService googleOAuthService;

    @PostMapping("/google")
    public ResponseEntity<AuthTokenResponse> callback(@RequestBody AuthorizationCodeRequest authorizationCodeRequest,
                                                      HttpServletResponse response){
        log.info("[callback] 구글 인가코드 발급 완료 = {}", authorizationCodeRequest.getCode());
        AuthTokenResponse authTokenResponse = googleOAuthService.process(authorizationCodeRequest.getCode(), response);
        log.info("[callback] 클라이언트에게 줄 response body = {}", authTokenResponse);
        return ResponseEntity.ok(authTokenResponse);
    }
}
