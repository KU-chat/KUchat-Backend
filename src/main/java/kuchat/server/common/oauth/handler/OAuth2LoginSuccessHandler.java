package kuchat.server.common.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.AuthToken;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.common.oauth.CustomOAuth2User;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;
    private static final String REFRESH_TOKEN = "Authorization-refresh";

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("[onAuthenticationSuccess] 사용자가 로그인 성공 이후!");

        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // 회원가입한 회원인 경우 (Role = GUEST)
            // email로 access token 발급, 요청 헤더에 추가 -> 회원 추가정보 작성 폼으로 리다이렉트
            // 나중에 정보 입력 다 받으면 Role.STUDENT 로 업데이트 시켜야함
            if (customOAuth2User.getRole() == Role.GUEST) {
                log.info("[SuccessHandler] GUEST, 아직 쿠챗 회원가입을 하지 않은 사람");
                String platform = customOAuth2User.getPlatform().getValue();
                String providerId = customOAuth2User.getProviderId();
                response.sendRedirect("/member/signup");
                String accessToken = "Bearer " + jwtTokenService.generateGuestToken(platform, providerId);
                response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
                log.info("[회원가입 전 access token] {}", accessToken);
                response.getWriter().flush();
            }

            // 기존 회원인 경우 (Role = STUDENT)
            // 로그인 처리 하기
            else {
                log.info("[SuccessHandler] STUDENT, 이미 쿠챗 회원인 사람");

                Member member = memberRepository.findByPlatformAndProviderId(customOAuth2User.getPlatform(),
                                customOAuth2User.getProviderId())
                        .orElseThrow(() -> new KuchatException(BaseResponse.NOT_FOUND_MEMBER));
                log.info("[SuccessHandler] 기존 회원인 경우 platform = {}, provider id = {}",
                        customOAuth2User.getPlatform(), customOAuth2User.getProviderId());
                AuthToken authToken = jwtTokenService.generateAuthToken(Role.STUDENT, member.getId());
                log.info("[SuccessHandler] accessToken : " + authToken.getAccessToken());
                log.info("[SuccessHandler] refreshToken : " + authToken.getRefreshToken());

                response.sendRedirect("/");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(new ObjectMapper().writeValueAsString(authToken));
                response.getWriter().flush();

                log.info("[SuccessHandler] 로그인 성공!!! 토큰 발급도 함!");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new KuchatException(BaseResponse.OAUTH2_FAIL);
        }
    }
}
