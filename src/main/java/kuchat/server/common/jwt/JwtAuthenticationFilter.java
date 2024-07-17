package kuchat.server.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// req에서 jwt token만 추출 -> 통과 시에만 권한 부여
// -> 실패하면 권한 부여X, 다음 필터 적용하기
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtTokenService.extractAccessToken(request).orElseThrow(
                () -> new KuchatException(BaseResponse.NOT_FOUND_TOKEN));

        if(jwtTokenService.validateToken(token)){
            Long memberId = jwtTokenService.extractMemberIdFromToken(token);
            if(memberId != null && SecurityContextHolder.getContext().getAuthentication() == null){     // 중복 인증을 방지
                UserDetails userDetails = jwtTokenService.getUserDetails(memberId);     // 사용자의 인증 및 권한 정보를 포함하는 spring security 의 사용자 정보 캡슐화 인터페이스를 가져온다.
                UsernamePasswordAuthenticationToken authenticationToken = jwtTokenService.getAuthenticationToken(token, userDetails);
                // userDatails 와 token 기반의 UsernamePasswordAuthenticationToken 객체 생성. 이 객체는 사용자의 인증 정보를 포함하는 객체로 이후 인증 과정을 위해 사용된다.
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));     // 현재 요청의 세부 정보를 설정한다.
                // HttpServletRequest 로부터 세부 정보를 생성하고 이를 authenticationToken 객체에 설정한다. = 요청의 IP 주소, 세션 id 정보 등이 인증 객체에 포함된다.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);      // 인증이 완료 됐다고 설정을 변경
            }
        }
        filterChain.doFilter(request, response);        // 다음 필터 또는 리소스로 request, response 를 전달하는 역할을 한다.
    }
}
