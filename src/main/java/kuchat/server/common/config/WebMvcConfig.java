package kuchat.server.common.config;

import kuchat.server.common.argumentResolver.AuthArgumentResolver;
import kuchat.server.common.jwt.JwtTokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    private final AuthArgumentResolver authArgumentResolver;

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
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
