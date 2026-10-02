package com.teacat.config;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

@Component
public class JwtUtil {

    private final String SECRET_KEY = System.getenv().getOrDefault(
            "JWT_SECRET", "TeaCatLocalDevelopmentSecretKey12345678901234567890");

    public String generateToken(
            String email
    ) {

        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(
                        StandardCharsets.UTF_8
                )
        );

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 86400000
                        )
                )
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(
            String token
    ) {
        SecretKey key = Keys.hmacShaKeyFor(
                SECRET_KEY.getBytes(
                        StandardCharsets.UTF_8
                )
        );
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

}