package com.fastify.user.annotation;

import com.fastify.user.annotation.resolver.UserClaimsArgumentResolver;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to inject {@link com.fastify.user.model.entity.User} into controller methods.
 * Resolved by {@link UserClaimsArgumentResolver}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CurrentUser {
}
