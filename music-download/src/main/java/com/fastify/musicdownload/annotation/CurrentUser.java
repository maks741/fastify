package com.fastify.musicdownload.annotation;

import com.fastify.musicdownload.annotation.resolver.UserClaimsArgumentResolver;

/**
 * Annotation used to inject {@link com.fastify.musicdownload.model.entity.User} into controller methods.
 * Resolved by {@link UserClaimsArgumentResolver}
 */
public @interface CurrentUser {
}
