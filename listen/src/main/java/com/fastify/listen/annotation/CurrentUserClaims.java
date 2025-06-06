package com.fastify.listen.annotation;

import com.fastify.listen.annotation.resolver.UserClaimsArgumentResolver;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to inject {@link com.fastify.listen.model.dto.user.UserClaims} into controller methods.
 * Resolved by {@link UserClaimsArgumentResolver}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CurrentUserClaims {
}
