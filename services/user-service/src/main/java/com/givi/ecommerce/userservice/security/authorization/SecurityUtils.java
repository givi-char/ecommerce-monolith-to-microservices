package com.givi.ecommerce.userservice.security.authorization;

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

        if (userId == null) {
            return false;
        }

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUserEmail =
                authentication.getName();

        if (currentUserEmail == null
                || currentUserEmail.isBlank()) {
            return false;
        }

        return userRepository
                .findById(userId)
                .map(user ->
                        currentUserEmail.equalsIgnoreCase(
                                user.getEmail()
                        )
                )
                .orElse(false);
    }

    public String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }
}