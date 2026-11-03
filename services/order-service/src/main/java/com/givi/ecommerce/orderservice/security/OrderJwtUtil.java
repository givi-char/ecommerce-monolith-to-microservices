package com.givi.ecommerce.orderservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class OrderJwtUtil {

    private final String accessSecret;

    public OrderJwtUtil(
            @Value("${security.jwt.access-secret}")
            String accessSecret
    ) {
        this.accessSecret = accessSecret;
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes =
                accessSecret.getBytes(
                        StandardCharsets.UTF_8
                );

        return Keys.hmacShaKeyFor(
                keyBytes
        );
    }

    public boolean validateToken(
            String token
    ) {

        try {

            Jwts.parser()
                    .verifyWith(
                            getSigningKey()
                    )
                    .build()
                    .parseSignedClaims(
                            token
                    );

            return true;

        } catch (
                JwtException
                | IllegalArgumentException exception
        ) {

            return false;
        }
    }

    private Claims getClaims(
            String token
    ) {

        return Jwts.parser()
                .verifyWith(
                        getSigningKey()
                )
                .build()
                .parseSignedClaims(
                        token
                )
                .getPayload();
    }

    public Long getUserId(
            String token
    ) {

        Number userId =
                getClaims(token)
                        .get(
                                "userId",
                                Number.class
                        );

        if (userId == null) {
            return null;
        }

        return userId.longValue();
    }

    public String getEmail(
            String token
    ) {

        return getClaims(token)
                .getSubject();
    }

    public List<String> getRoles(
            String token
    ) {

        Object roles =
                getClaims(token)
                        .get("roles");

        if (roles instanceof List<?>) {

            return ((List<?>) roles)
                    .stream()
                    .map(Object::toString)
                    .toList();
        }

        return List.of();
    }
}