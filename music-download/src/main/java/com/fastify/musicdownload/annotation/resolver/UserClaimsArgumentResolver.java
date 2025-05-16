package com.fastify.musicdownload.annotation.resolver;

import com.fastify.musicdownload.annotation.CurrentUserClaims;
import com.fastify.musicdownload.constant.AppConstant;
import com.fastify.musicdownload.exception.NoJwtException;
import com.fastify.musicdownload.model.dto.user.UserClaims;
import com.fastify.musicdownload.security.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
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
    public UserClaims resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory
    ) {
        String authorizationHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(AppConstant.BEARER_TOKEN_PREFIX)) {
            throw new NoJwtException("No JWT found in request header");
        }
        String jwt = authorizationHeader.substring(AppConstant.BEARER_TOKEN_PREFIX.length());
        return jwtService.extractUserClaims(jwt);
    }
}
