package kuchat.server.common.config;

import kuchat.server.common.jwt.argumentResolver.AuthArgumentResolver;
import kuchat.server.common.jwt.JwtTokenInterceptor;
import kuchat.server.common.jwt.argumentResolver.GuestArgumentResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private final AuthArgumentResolver authArgumentResolver;
    private final GuestArgumentResolver guestArgumentResolver;
    private final JwtTokenInterceptor jwtTokenInterceptor;


    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MustacheViewResolver viewResolver = new MustacheViewResolver();
        viewResolver.setCharset("UTF-8");
        viewResolver.setContentType("text/html; charset=utf-8");
        viewResolver.setPrefix("classpath:/templates/");
        viewResolver.setSuffix(".html");
        registry.viewResolver(viewResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("[interceptor 등록]");
        registry.addInterceptor(jwtTokenInterceptor)
                .excludePathPatterns("/member/signup/**")
                .addPathPatterns("/member/my-profile", "/friend/**", "/block/**", "/chatroom/**");
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
        resolvers.add(guestArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("[addCorsMappings] CorsMapping 호출");
        registry.addMapping("/**")
                .allowedOrigins("https://kuchat.netlify.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .exposedHeaders("*")
                .allowedHeaders("*")
                .allowCredentials(true);        // 쿠키 허용
    }

}
