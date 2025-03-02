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
        Member member = memberService.lookupMemberByGoogleId(infoResponse.getId());
        return memberService.processLoginOrSignup(member, tokenResponse, infoResponse, httpServletResponse);
    }

    public GoogleTokenResponse requestAccessToken(String code) {
        log.info("Requesting Google access token for code: {}", code);

        Map<String, String> requestBody = Map.of(
                "code", code,
                "client_id", CLIENT_ID,
                "client_secret", CLIENT_SECRET,
                "redirect_uri", REDIRECT_URI,
                "grant_type", "authorization_code"
        );

        return restClient.post()
                .uri("/token")
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body(requestBody)
                .retrieve()
                .body(GoogleTokenResponse.class);
    }

    public GoogleInfoResponse requestUserInfo(String accessToken) {
        log.info("Requesting Google user info with access token");

        JsonNode userInfoNode = restClient.get()
                .uri("/oauth2/v2/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(JsonNode.class);

        return new GoogleInfoResponse(
                userInfoNode.get("id").asText(),
                userInfoNode.get("email").asText(),
                userInfoNode.get("name").asText(),
                userInfoNode.get("picture").asText()
        );
    }
}
