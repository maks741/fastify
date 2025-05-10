package com.fastify.musicdownload.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String jwtSigningKey;

    public JwtService(
            @Value("${jwt.signing.key}") String jwtSigningKey
    ) {
        this.jwtSigningKey = jwtSigningKey;
    }

    public String extractUsername(String jwt) {
        return extractClaims(jwt, Claims::getSubject);
    }

    public boolean validate(String jwt) {
        return tokenNotExpired(jwt);
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
