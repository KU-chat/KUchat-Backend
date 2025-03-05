package kuchat.server.domain.oauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.auth.service.JwtTokenService;
import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static kuchat.server.common.response.BaseResponseStatus.SUCCESS;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2LogoutSuccessHandler implements LogoutSuccessHandler {

    private final MemberService memberService;
    private final JwtTokenService jwtTokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            String accessToken = request.getHeader("Authorization");
            Member member = jwtTokenService.extractMemberByAccessToken(accessToken);
            memberService.logout(member);

            log.info("[onLogoutSuccess] 로그아웃 요청 사용자 id = {}, name = {}" + member.getId(), member.getName());
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(new BaseResponse(SUCCESS));

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        } catch (KuchatException e){
            log.error("[onLogoutSuccess] 로그아웃 처리 중 예외 발생", e.getMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponse = objectMapper.writeValueAsString(e.getResponse());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(e.getResponse().getHttpStatus().value());
            response.getWriter().write(errorResponse);
            response.getWriter().flush();
        }
    }
}
