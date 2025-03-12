//package kuchat.server.common.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Slf4j
//@RequiredArgsConstructor
//@Configuration
//@EnableWebSecurity          // spring security 기능을 활성화시키는 어노테이션 (스프링 시큐리티 필터가 스프링 필터 체인에 등록됨)
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        return http
//                .httpBasic(AbstractHttpConfigurer::disable)                               // jwt 토큰을 사용한 bearer 방식을 사용하므로 default 설정 disable
//                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))      // h2 콘솔에 접근하기 위해서는 X-Frame-Options Click jacking 공격을 막는 설정 disable
//                .csrf(csrf -> csrf.ignoringRequestMatchers("ws-connect/**"))        // websocket 경로는 CSRF 예외 처리                                       // rest api 방식에서는 jwt 또는 oauth2 방식을 사용하여 인증하므로 csrf 보안기능 필요 X
//                .sessionManagement(sessionManagement ->
//                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)             // 세션을 사용하지 않으므로 disable (stateless)
//                )
//                .authorizeHttpRequests((authorize) ->
//                        authorize.anyRequest().permitAll()
//                )
//                .build();
//    }
//}
