package com.fastify.download.annotation.resolver;

import com.fastify.download.annotation.CurrentUserClaims;
import com.fastify.download.model.dto.user.UserClaims;
import com.fastify.download.security.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class UserClaimsArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUserClaims.class) != null
                && parameter.getParameterType().isAssignableFrom(UserClaims.class);
    }

    @Override
    public UserClaims resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String jwt = jwtService.extractJwt(webRequest);
        return jwtService.extractUserClaims(jwt);
    }
}
