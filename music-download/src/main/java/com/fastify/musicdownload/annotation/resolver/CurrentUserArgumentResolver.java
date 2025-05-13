package com.fastify.musicdownload.annotation.resolver;

import com.fastify.musicdownload.annotation.CurrentUser;
import com.fastify.musicdownload.constant.AppConstant;
import com.fastify.musicdownload.constant.JwtConstant;
import com.fastify.musicdownload.exception.ClaimNotFoundException;
import com.fastify.musicdownload.exception.NoJwtException;
import com.fastify.musicdownload.model.entity.User;
import com.fastify.musicdownload.security.JwtService;
import com.fastify.musicdownload.service.UserService;
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
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
                && parameter.getParameterType().isAssignableFrom(User.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory
    ) {
        String authorizationHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(AppConstant.BEARER_TOKEN_PREFIX)) {
            throw new NoJwtException("No JWT found in request header");
        }
        String jwt = authorizationHeader.substring(AppConstant.BEARER_TOKEN_PREFIX.length());
        Long userId = jwtService.extractClaims(jwt, claims -> claims.get(JwtConstant.JWT_USER_ID_CLAIM, Long.class));

        if (userId == null) {
            throw new ClaimNotFoundException("Claim userId was not found in JWT");
        }

        return userService.findById(userId);
    }
}
