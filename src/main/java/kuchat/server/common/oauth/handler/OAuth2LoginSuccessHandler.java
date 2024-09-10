package kuchat.server.common.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.AuthToken;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.common.oauth.CustomOAuth2User;
import kuchat.server.common.redis.RedisService;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final RedisService redisService;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
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
                String guestToken = jwtTokenService.generateGuestToken(platform, providerId);
                log.info("[SuccessHandler] guest token 생성 = {}", guestToken);

                // 프론트와 api 연동 시 redirect url
//                String url = "http://localhost:3000/signup?guest-token=" + guestToken;

                // 백에서 테스트 시 redirect url
                String url = "http://localhost:9000/member/signup?guest-token=" + guestToken;

                log.info("[redirect url] " + url);
                response.sendRedirect(url);
            }

            // 기존 회원인 경우 (Role = STUDENT)
            // 로그인 처리 하기
            else {
                log.info("[SuccessHandler] STUDENT, 이미 쿠챗 회원인 사람");

                Member member = memberRepository.findByPlatformAndProviderId(customOAuth2User.getPlatform(),
                                customOAuth2User.getProviderId())
                        .orElseThrow(() -> new KuchatException(BaseResponseStatus.NOT_FOUND_MEMBER));
                log.info("[SuccessHandler] 기존 회원인 경우 platform = {}, provider id = {}",
                        customOAuth2User.getPlatform(), customOAuth2User.getProviderId());
                AuthToken authToken = jwtTokenService.generateAuthToken(Role.STUDENT, member.getId());
                log.info("[SuccessHandler] accessToken : " + authToken.getAccessToken());
                log.info("[SuccessHandler] refreshToken : " + authToken.getRefreshToken());

//                response.sendRedirect("http://localhost:3000/user");
                response.sendRedirect("/");
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(new ObjectMapper().writeValueAsString(authToken));
                response.getWriter().flush();

                log.info("[SuccessHandler] 로그인 성공!!! 토큰 발급도 함!");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new KuchatException(BaseResponseStatus.OAUTH2_FAIL);
        }
    }
}
