package com.givi.ecommerce.userservice.auth.service.impl;

import com.givi.ecommerce.userservice.auth.entity.RefreshToken;
import com.givi.ecommerce.userservice.auth.repository.RefreshTokenRepository;
import com.givi.ecommerce.userservice.auth.service.RefreshTokenService;
import com.givi.ecommerce.userservice.entity.User;
import com.givi.ecommerce.userservice.exception.InvalidRefreshTokenException;
import com.givi.ecommerce.userservice.security.jwt.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final JwtUtil jwtUtil;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository repository,
            JwtUtil jwtUtil
    ) {
        this.repository = repository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public RefreshToken createOrReplaceRefreshToken(
            User user
    ) {

        repository.deleteByUsername(
                user.getEmail()
        );

        String token =
                jwtUtil.generateRefreshToken(
                        user.getEmail()
                );

        Instant expiry =
                Instant.now().plusSeconds(
                        jwtUtil.getRefreshExpirationSeconds()
                );

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUsername(
                user.getEmail()
        );

        refreshToken.setToken(token);
        refreshToken.setExpiresAt(expiry);

        return repository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken validateAndGet(
            String token
    ) {

        if (!jwtUtil.validateRefreshToken(token)) {
            throw new InvalidRefreshTokenException(
                    "Invalid or expired refresh token"
            );
        }

        RefreshToken refreshToken =
                repository.findByToken(token)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Refresh token not found"
                                )
                        );

        if (refreshToken
                .getExpiresAt()
                .isBefore(Instant.now())) {

            repository.delete(refreshToken);

            throw new InvalidRefreshTokenException(
                    "Refresh token expired"
            );
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void deleteByUsername(
            String username
    ) {
        repository.deleteByUsername(username);
    }

    @Override
    @Transactional
    public void deleteByToken(
            String token
    ) {
        repository.deleteByToken(token);
    }
}