package kuchat.server.common.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.oauth.CustomOAuth2User;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.jwt.JwtTokenService;
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

    @Transactional
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("[onAuthenticationSuccess] 로그인 성공 이후!");
        try {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            // 회원가입한 회원인 경우 (Role = GUEST)
            // email로 access token 발급, 요청 헤더에 추가 -> 회원 추가정보 작성 폼으로 리다이렉트
            // 나중에 정보 입력 다 받으면 Role.STUDENT 로 업데이트 시켜야함
            if (customOAuth2User.getRole() == Role.GUEST) {

                String accessToken = jwtTokenService.generateAccessToken(customOAuth2User.getEmail());
                log.info("[onAuthenticationSuccess] accessToken : " + accessToken);
                response.addHeader(jwtTokenService.getAccessHeader(), "Bearer " + accessToken);
                jwtTokenService.sendAccessAndRefreshToken(response, accessToken, null);
                log.info("[onAuthenticationSuccess] sendAccessAndRefreshToken 이후");
                String platform = customOAuth2User.getPlatform().getValue();
                String attributeName = customOAuth2User.getName();
                String redirectUrl = "/member/signup?platform=" + platform + "&attributeName=" + attributeName;
                response.sendRedirect(redirectUrl);
                log.info("[sendAccessAndRefreshToken] send redirect 이후");
                return;
            }

            // 기존 회원인 경우 (Role = STUDENT)
            // 로그인 처리 하기
            else {
                String accessToken = jwtTokenService.generateAccessToken(customOAuth2User.getEmail());
                String refreshToken = jwtTokenService.generateRefreshToken();
                log.info("accessToken : " + accessToken);
                log.info("refreshToken : " + refreshToken);

                response.addHeader(jwtTokenService.getAccessHeader(), "Bearer " + accessToken);
                response.addHeader(jwtTokenService.getRefreshHeader(), "Bearer " + refreshToken);

                jwtTokenService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
                jwtTokenService.updateRefreshToken(customOAuth2User.getEmail(), refreshToken);

                response.sendRedirect("/");
            }
        } catch (Exception e) {
            throw new KuchatException(BaseResponse.OAUTH2_FAIL);
        }
    }
}
