package com.fastify.auth.security;

import com.fastify.auth.model.entity.User;
import com.fastify.auth.model.enumeration.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final String jwtSigningKey;
    private final Long jwtExpirationPeriod;
    private final String userIdClaimName;
    private final String userEmailClaimName;
    private final String userRoleClaimName;

    public JwtService(
            @Value("${jwt.signing.key}") String jwtSigningKey,
            @Value("${jwt.expiration.period}") Long jwtExpirationPeriod,
            @Value("${jwt.claims.user.id}") String userIdClaimName,
            @Value("${jwt.claims.user.email}") String userEmailClaimName,
            @Value("${jwt.claims.user.role}") String userRoleClaimName
    ) {
        this.jwtSigningKey = jwtSigningKey;
        this.jwtExpirationPeriod = jwtExpirationPeriod;
        this.userIdClaimName = userIdClaimName;
        this.userEmailClaimName = userEmailClaimName;
        this.userRoleClaimName = userRoleClaimName;
    }

    public String generateToken(User user) {
        Long userId = user.getId();
        String userEmail = user.getEmail();
        Role userRole = user.getRole();
        Map<String, Object> claims = Map.of(
                userIdClaimName, userId,
                userEmailClaimName, userEmail,
                userRoleClaimName, userRole
        );

        return generateToken(claims, user);
    }

    public String generateToken(
            Map<String, Object> claims,
            UserDetails userDetails
    ) {
        long currentTimeMillis = System.currentTimeMillis();
        Date issuedAt = new Date(currentTimeMillis);
        Date expiration = new Date(currentTimeMillis + jwtExpirationPeriod);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(signingKey())
                .compact();
    }

    private boolean tokenNotExpired(String jwt) {
        Date expirationDate = extractClaims(jwt, Claims::getExpiration);
        Date now = new Date(System.currentTimeMillis());
        return expirationDate.after(now);
    }

    private <T> T extractClaims(String jwt, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(jwt);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private SecretKey signingKey() {
        byte[] keyBytes = jwtSigningKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
