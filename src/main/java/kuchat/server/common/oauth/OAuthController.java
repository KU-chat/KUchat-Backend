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
//    public ResponseEntity<AuthTokenResponse> callback(@RequestParam String code,
                                                      HttpServletResponse response){
        log.info("[callback] 구글 인가코드 발급 완료 = {}", authRequest.getCode());
//        log.info("[callback] 구글 인가코드 발급 완료 = {}", code);
        AuthTokenResponse authTokenResponse = oAuthService.process(authRequest.getCode(), response);
//        AuthTokenResponse authTokenResponse = oAuthService.process(code, response);
        return ResponseEntity.ok(authTokenResponse);
    }
    // 4%2F0AQSTgQFPh-pHRCB98D8FvcM_cxJo1eRnWtUN6EM5teF4UuaZiACuin0g4Qwt1sUSujQUFQ
}
