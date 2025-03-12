package kuchat.server.domain.oauth.controller;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.auth.dto.AuthTokenResponse;
import kuchat.server.domain.auth.dto.GuestTokenResponse;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.service.MemberService;
import kuchat.server.domain.oauth.dto.AuthorizationCodeRequest;
import kuchat.server.domain.oauth.dto.GoogleInfoResponse;
import kuchat.server.domain.oauth.service.GoogleOAuthService;
import kuchat.server.domain.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kuchat.server.domain.enums.Role.GUEST;

@Slf4j
@RequestMapping("/oauth")
@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final GoogleOAuthService googleOAuthService;
    private final MemberService memberService;

    @PostMapping("/google")
    public ResponseEntity<BaseResponse> callback(@RequestBody AuthorizationCodeRequest authorizationCodeRequest) {
        log.info("[callback] 구글 인가코드 발급 완료 = {}", authorizationCodeRequest.getCode());
        GoogleInfoResponse userInfoResponse = googleOAuthService.getUserInfo(authorizationCodeRequest.getCode());
        Member member = memberService.lookupMemberByGoogleInfo(userInfoResponse);
        if (member.getRole() == GUEST) {
            GuestTokenResponse guestTokenResponse = memberService.handleGuest(member);
            return ResponseEntity.ok(guestTokenResponse);
        }
        AuthTokenResponse authTokenResponse = memberService.handleStudent(member);
        return CookieUtil.setAuthToken(authTokenResponse);
    }
}
