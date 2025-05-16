package com.fastify.musicdownload.annotation.resolver;

import com.fastify.musicdownload.annotation.CurrentUser;
import com.fastify.musicdownload.constant.AppConstant;
import com.fastify.musicdownload.exception.NoJwtException;
import com.fastify.musicdownload.model.dto.user.UserClaims;
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
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
                && parameter.getParameterType().isAssignableFrom(User.class);
    }

    @Override
    public User resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String jwt = jwtService.extractJwt(webRequest);
        UserClaims userClaims = jwtService.extractUserClaims(jwt);
        return userService.findById(userClaims.userId());
    }
}
