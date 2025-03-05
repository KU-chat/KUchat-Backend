package kuchat.server.common.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.oauth.dto.GoogleInfoResponse;
import kuchat.server.common.oauth.dto.GoogleTokenResponse;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.AuthTokenResponse;
import kuchat.server.domain.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class OAuthService {

    private final MemberService memberService;
    private final RestClient restClient;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String REDIRECT_URI;


    public OAuthService(MemberService memberService, RestClient.Builder builder) {
        this.memberService = memberService;
        this.restClient = builder
                .baseUrl("https://oauth2.googleapis.com")
                .build();
    }

    public AuthTokenResponse process(String code, HttpServletResponse httpServletResponse) {
        GoogleTokenResponse tokenResponse = requestAccessToken(code);
        GoogleInfoResponse infoResponse = requestUserInfo(tokenResponse.getAccessToken());
        log.info("[process] 구글에서 제공한 user info = {}", infoResponse);
        Member member = memberService.lookupMemberByGoogleId(infoResponse.getId());
        return memberService.processLoginOrSignup(member, tokenResponse, infoResponse, httpServletResponse);
    }

    public GoogleTokenResponse requestAccessToken(String code) {
        log.info("[requestAccessToken] google에 토큰을 요청하기 위해 필요한 인가코드 = {}", code);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", CLIENT_ID);
        requestBody.add("client_secret", CLIENT_SECRET);
        requestBody.add("code", code);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("redirect_uri", REDIRECT_URI);
        log.info("[requestAccessToken] 요청을 보낼 body = {}", requestBody.toString());

        return restClient.post()
                .uri("/token")
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body(requestBody)
                .retrieve()
                .body(GoogleTokenResponse.class);
    }

    public GoogleInfoResponse requestUserInfo(String accessToken) {
        log.info("[requestUserInfo] 구글에 사용자 정보를 요청하기 위해 필요한 access token = {}", accessToken);

        return restClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toEntity(GoogleInfoResponse.class)
                .getBody();
    }
}
