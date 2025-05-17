package com.fastify.listen.security;

import com.fastify.listen.constant.AppConstant;
import com.fastify.listen.exception.NoJwtException;
import com.fastify.listen.model.dto.user.UserClaims;
import com.fastify.listen.model.enumeration.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String jwtSigningKey;
    private final String userIdClaimName;
    private final String userRoleClaimName;

    public JwtService(
            @Value("${jwt.signing.key}") String jwtSigningKey,
            @Value("${jwt.claims.user.id}") String userIdClaimName,
            @Value("${jwt.claims.user.role}") String userRoleClaimName
    ) {
        this.jwtSigningKey = jwtSigningKey;
        this.userIdClaimName = userIdClaimName;
        this.userRoleClaimName = userRoleClaimName;
    }

    public String extractUsername(String jwt) {
        return extractClaims(jwt, Claims::getSubject);
    }

    public UserClaims extractUserClaims(String jwt) {
        return extractClaims(jwt, claims -> new UserClaims(
                claims.get(userIdClaimName, Long.class),
                claims.getSubject(),
                Role.valueOf(claims.get(userRoleClaimName, String.class))
        ));
    }

    public boolean validate(String jwt) {
        return tokenNotExpired(jwt);
    }

    public <T> T extractClaims(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(jwt);
        return claimsResolver.apply(claims);
    }

    public String extractJwt(NativeWebRequest webRequest) {
        String authorizationHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(AppConstant.BEARER_TOKEN_PREFIX)) {
            throw new NoJwtException("No JWT found in request header");
        }
        return authorizationHeader.substring(AppConstant.BEARER_TOKEN_PREFIX.length());
    }

    private Claims extractClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private boolean tokenNotExpired(String jwt) {
        Date expirationDate = extractClaims(jwt, Claims::getExpiration);
        Date now = new Date(System.currentTimeMillis());
        return expirationDate.after(now);
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtSigningKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
