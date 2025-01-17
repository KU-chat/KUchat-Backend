package kuchat.server.common.config;

import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.jwt.JwtTokenInterceptor;
import kuchat.server.common.oauth.handler.OAuth2LoginFailureHandler;
import kuchat.server.common.oauth.handler.OAuth2LoginSuccessHandler;
import kuchat.server.common.oauth.handler.OAuth2LogoutSuccessHandler;
import kuchat.server.common.oauth.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity          // spring security 기능을 활성화시키는 어노테이션 (스프링 시큐리티 필터가 스프링 필터 체인에 등록됨)
public class SecurityConfig {

    private final OAuth2Service oAuth2Service;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final OAuth2LogoutSuccessHandler oAuth2LogoutSuccessHandler;
    private final JwtTokenInterceptor jwtTokenInterceptor;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)                               // jwt 토큰을 사용한 bearer 방식을 사용하므로 default 설정 disable
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))      // h2 콘솔에 접근하기 위해서는 X-Frame-Options Click jacking 공격을 막는 설정 disable
                .csrf(AbstractHttpConfigurer::disable)                                              // rest api 방식에서는 jwt 또는 oauth2 방식을 사용하여 인증하므로 csrf 보안기능 필요 X
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)             // 세션을 사용하지 않으므로 disable (stateless)
                )
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().permitAll()
                )
//                .authorizeHttpRequests((authorize) -> authorize                             // 인증, 인가 설정 시 HttpServletRequest 를 사용한다는 의미
//                        .requestMatchers("/index.html", "/", "/css/**", "/images/**", "/js/**", "/h2-console/**",
//                                "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/oauth/login",
//                                "/member/signup", "/publish/**","/subscribe/**",  "/ws-connect").permitAll()       // 인증 절차 없이 접근 가능해야 하는 페이지 모두 추가하기
//                        .anyRequest().authenticated()           // 이 외에 모든 페이지는 인증된 사용자만 접근 가능
//                )
                .oauth2Login(oauth2Login -> oauth2Login                                      // oauth2 로그인에 관한 다양한 기능 제공
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2Service))            // oauth2 로그인 로직을 담당하는 service 등록
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("Authorization")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .addLogoutHandler((request, response, auth) -> {
                            // 추가 로그아웃 처리 로직 (선택)
                            System.out.println("Custom logout handler executed");
                        })
                        .logoutSuccessHandler(oAuth2LogoutSuccessHandler)
                )
                .build();
    }
}
