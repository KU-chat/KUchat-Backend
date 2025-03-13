package kuchat.server.common.config;

import kuchat.server.domain.auth.argumentResolver.AuthArgumentResolver;
import kuchat.server.domain.auth.argumentResolver.GuestArgumentResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthArgumentResolver authArgumentResolver;
    private final GuestArgumentResolver guestArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
        resolvers.add(guestArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("[addCorsMappings] CorsMapping 호출");
        registry.addMapping("/**")
                .allowedOrigins("https://kuchat.netlify.app", "http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .exposedHeaders("Authorization", "Set-Cookie")
                .allowCredentials(true)     // 쿠키 허용
                .maxAge(3000);      // 원하는 시간만큼 pre-flight 리퀘스트를 캐싱
    }

}
