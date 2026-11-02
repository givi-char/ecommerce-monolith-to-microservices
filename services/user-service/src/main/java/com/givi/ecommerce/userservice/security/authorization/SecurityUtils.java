package com.givi.ecommerce.userservice.security.authorization;

import com.givi.ecommerce.userservice.entity.User;
import com.givi.ecommerce.userservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityUtils")
public class SecurityUtils {

    private final UserRepository userRepository;

    public SecurityUtils(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public boolean isUserId(Long userId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return false;
        }

        String email = authentication.getName();

        if (email == null
                || email.isBlank()
                || "anonymousUser".equals(email)) {

            return false;
        }

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        return user != null
                && user.getId().equals(userId);
    }
}