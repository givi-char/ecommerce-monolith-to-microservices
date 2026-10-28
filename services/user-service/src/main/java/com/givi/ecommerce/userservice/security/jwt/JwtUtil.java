package com.givi.ecommerce.userservice.security.jwt;

import com.givi.ecommerce.userservice.config.properties.JwtProperties;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private Key getAccessSigningKey() {
        byte[] keyBytes = jwtProperties
                .getAccessSecret()
                .getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getRefreshSigningKey() {
        byte[] keyBytes = jwtProperties
                .getRefreshSecret()
                .getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(
            String username,
            Collection<String> roles
    ) {
        Date now = new Date();

        Date expiry = new Date(
                now.getTime()
                        + jwtProperties.getAccessTokenExpiration()
        );

        JwtBuilder builder = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry);

        if (roles != null && !roles.isEmpty()) {
            builder.claim(
                    "roles",
                    roles.stream().toList()
            );

            builder.claim(
                    "role",
                    roles.iterator().next()
            );
        }

        return builder
                .signWith(
                        getAccessSigningKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public String generateRefreshToken(String username) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime()
                        + jwtProperties.getRefreshTokenExpiration()
        );

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("type", "refresh")
                .signWith(
                        getRefreshSigningKey(),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getAccessSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getRefreshSigningKey())
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    public String getUsernameFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getAccessSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUsernameFromRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getRefreshSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public long getRefreshExpirationSeconds() {
        return jwtProperties.getRefreshTokenExpiration() / 1000;
    }
}