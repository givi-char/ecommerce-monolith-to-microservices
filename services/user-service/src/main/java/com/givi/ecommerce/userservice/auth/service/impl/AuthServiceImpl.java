package com.givi.ecommerce.userservice.auth.service.impl;

import com.givi.ecommerce.userservice.auth.dto.AuthResponse;
import com.givi.ecommerce.userservice.auth.dto.LoginRequest;
import com.givi.ecommerce.userservice.auth.dto.SignupRequest;
import com.givi.ecommerce.userservice.auth.dto.SignupResponse;
import com.givi.ecommerce.userservice.auth.entity.RefreshToken;
import com.givi.ecommerce.userservice.auth.service.AuthService;
import com.givi.ecommerce.userservice.auth.service.RefreshTokenService;
import com.givi.ecommerce.userservice.config.properties.SecurityProperties;
import com.givi.ecommerce.userservice.entity.User;
import com.givi.ecommerce.userservice.enums.AuthProvider;
import com.givi.ecommerce.userservice.enums.Role;
import com.givi.ecommerce.userservice.enums.SellerStatus;
import com.givi.ecommerce.userservice.enums.UserStatus;
import com.givi.ecommerce.userservice.exception.DuplicateResourceException;
import com.givi.ecommerce.userservice.exception.InvalidCredentialsException;
import com.givi.ecommerce.userservice.exception.InvalidRefreshTokenException;
import com.givi.ecommerce.userservice.repository.UserRepository;
import com.givi.ecommerce.userservice.security.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final SecurityProperties securityProperties;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            RefreshTokenService refreshTokenService,
            SecurityProperties securityProperties
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.securityProperties = securityProperties;
    }

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User with email " + request.getEmail() + " already exists"
            );
        }

        User user = new User();

        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setProvider(AuthProvider.LOCAL);
        user.setStatus(UserStatus.ACTIVE);
        user.setSellerStatus(SellerStatus.NOT_A_SELLER);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        return new SignupResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                "USER"
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user != null && isAccountLocked(user)) {
            throw new InvalidCredentialsException(
                    "Account is locked. Please try again later."
            );
        }

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (AuthenticationException exception) {

            if (user != null) {
                handleFailedLogin(user);
            }

            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException(
                    "User account is not active"
            );
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);

        userRepository.save(user);

        return createAuthenticationResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {

        RefreshToken storedToken =
                refreshTokenService.validateAndGet(refreshToken);

        String username =
                jwtUtil.getUsernameFromRefreshToken(
                        storedToken.getToken()
                );

        User user = userRepository
                .findByEmail(username)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "User for refresh token not found"
                        )
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidRefreshTokenException(
                    "User account is not active"
            );
        }

        return createAuthenticationResponse(user);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {

        refreshTokenService.deleteByToken(
                refreshToken
        );
    }

    private void handleFailedLogin(User user) {

        int currentAttempts =
                user.getFailedLoginAttempts() == null
                        ? 0
                        : user.getFailedLoginAttempts();

        int newAttempts =
                currentAttempts + 1;

        user.setFailedLoginAttempts(
                newAttempts
        );

        if (newAttempts
                >= securityProperties.getMaxFailedAttempts()) {

            user.setLockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(
                                    securityProperties
                                            .getLockoutDurationMinutes()
                            )
            );
        }

        userRepository.save(user);
    }

    private boolean isAccountLocked(User user) {

        if (user.getLockedUntil() == null) {
            return false;
        }

        if (user.getLockedUntil()
                .isAfter(LocalDateTime.now())) {

            return true;
        }

        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);

        return false;
    }

    private AuthResponse createAuthenticationResponse(
            User user
    ) {

        Set<String> tokenRoles =
                user.getRoles()
                        .stream()
                        .map(Role::name)
                        .collect(Collectors.toSet());

        String accessToken =
                jwtUtil.generateAccessToken(
                        user.getId(),
                        user.getEmail(),
                        tokenRoles
                );

        RefreshToken refreshToken =
                refreshTokenService
                        .createOrReplaceRefreshToken(user);

        Set<String> responseRoles =
                user.getRoles()
                        .stream()
                        .map(role ->
                                role.name()
                                        .replace(
                                                "ROLE_",
                                                ""
                                        )
                        )
                        .collect(Collectors.toSet());

        return new AuthResponse(
                accessToken,
                user.getId(),
                user.getEmail(),
                responseRoles,
                "Bearer",
                refreshToken.getToken(),
                refreshToken
                        .getExpiresAt()
                        .toEpochMilli()
        );
    }
}