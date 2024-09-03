package kuchat.server.common.jwt.argumentResolver;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_TOKEN;

@RequiredArgsConstructor
@Component
public class GuestArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenService jwtTokenService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Guest.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String token = webRequest.getHeader("Authorization");
        if (token == null) {
            throw new KuchatException(NOT_FOUND_TOKEN);
        }
        String guestToken = token.replaceAll("Bearer ", "");
        return jwtTokenService.extractMemberByGuestToken(guestToken);
    }

}