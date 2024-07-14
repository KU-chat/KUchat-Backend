package kuchat.server.common.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.oauth.CustomOAuth2User;
import kuchat.server.common.oauth.dto.TokenResponse;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.jwt.JwtTokenService;
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
    private final ObjectMapper objectMapper;

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
                String accessToken = jwtTokenService.generateGUESTAccessToken(customOAuth2User.getEmail());
                log.info("[SuccessHandler] accessToken : " + accessToken);
//                jwtTokenService.sendAccessAndRefreshToken(response, accessToken, null);
                TokenResponse tokenResponse = new TokenResponse(accessToken);
                log.info("[SuccessHandler] tokenResponse = {}", tokenResponse.toString());
//                log.info("[onAuthenticationSuccess] sendAccessAndRefreshToken 이후");
                String platform = customOAuth2User.getPlatform().getValue();
                String attributeName = customOAuth2User.getName();
                String redirectUrl = "/member/signup?platform=" + platform + "&attributeName=" + attributeName;
                response.sendRedirect(redirectUrl);
                response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
                response.getWriter().flush();
//                response.addHeader(jwtTokenService.getAccessHeader(), "Bearer " + accessToken);
                log.info("[SuccessHandler] send redirect 이후");
            }

            // 기존 회원인 경우 (Role = STUDENT)
            // 로그인 처리 하기
            else {
                log.info("[SuccessHandler] STUDENT, 이미 쿠챗 회원인 사람");

                Member member = memberRepository.findByPlatformAndAttributeName(customOAuth2User.getPlatform(),
                        customOAuth2User.getAttribute("sub")).orElseThrow(() -> new KuchatException(BaseResponse.NOT_FOUND_MEMBER));
                log.info("[onAuthenticationSuccess] 기존 회원인 경우 platform = {}, attributeName = {}",
                        customOAuth2User.getPlatform(), customOAuth2User.getAttribute("sub"));
                String accessToken = jwtTokenService.generateSTUDENTAccessToken(member);
                String refreshToken = jwtTokenService.generateRefreshToken();
                log.info("accessToken : " + accessToken);
                log.info("refreshToken : " + refreshToken);

                response.addHeader(jwtTokenService.getAccessHeader(), "Bearer " + accessToken);
                response.addHeader(jwtTokenService.getRefreshHeader(), "Bearer " + refreshToken);

                jwtTokenService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
                jwtTokenService.updateRefreshToken(member.getId(), refreshToken);

                response.sendRedirect("/");
            }
        } catch (Exception e) {
            throw new KuchatException(BaseResponse.OAUTH2_FAIL);
        }
    }
}
