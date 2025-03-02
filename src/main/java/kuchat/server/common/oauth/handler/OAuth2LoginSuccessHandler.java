package kuchat.server.common.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.AuthToken;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.common.oauth.CustomOAuth2User;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.SignupResponse;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static kuchat.server.common.response.BaseResponseStatus.OAUTH2_FAIL;
import static kuchat.server.common.response.BaseResponseStatus.SUCCESS;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenService jwtTokenService;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
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

                response.sendRedirect("https://kuchat.netlify.app/signup?guest-token=" + guestToken);
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
                log.info("[SuccessHandler] 로그인 성공!!! 토큰 발급 완료 access token = {}, refresh token = {}",
                        authToken.getAccessToken(), authToken.getRefreshToken());

                setAuthCookie(response, "accessToken", authToken.getAccessToken());
                setAuthCookie(response, "refreshToken", authToken.getRefreshToken());
//                response.sendRedirect("https://kuchat.netlify.app/");
            }
        } catch (Exception e) {
            log.error("[onAuthenticationSuccess] 로그아웃 처리 중 예외 발생", e.getMessage());
            BaseResponse error = new BaseResponse(OAUTH2_FAIL);
            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponse = objectMapper.writeValueAsString(error);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(OAUTH2_FAIL.getHttpStatus().value());
            response.getWriter().write(errorResponse);
            response.getWriter().flush();

//            response.sendRedirect("https://kuchat.netlify.app/");
        }
    }

    private void setAuthCookie(HttpServletResponse response, String name, String token) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true); // XSS 공격 방지
        cookie.setSecure(true);   // HTTPS 에서만 전송 (개발 환경에서는 설정 비활성화 가능)
        cookie.setDomain("https://www.kuchat.site");      // 쿠키 경로 설정
        cookie.setMaxAge(60 * 60 * 24); // 쿠키 만료 시간 설정 (1시간)
        response.addCookie(cookie);
    }
}
