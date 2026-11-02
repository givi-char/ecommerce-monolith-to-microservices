package com.givi.ecommerce.userservice.auth.service;

import com.givi.ecommerce.userservice.auth.entity.RefreshToken;
import com.givi.ecommerce.userservice.entity.User;

public interface RefreshTokenService {

    RefreshToken createOrReplaceRefreshToken(
            User user
    );

    RefreshToken validateAndGet(
            String token
    );

    void deleteByUsername(
            String username
    );

    void deleteByToken(
            String token
    );
}