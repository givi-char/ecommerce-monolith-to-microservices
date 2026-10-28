package com.givi.ecommerce.userservice.auth.service.impl;

import com.givi.ecommerce.userservice.auth.dto.AuthResponse;
import com.givi.ecommerce.userservice.auth.dto.LoginRequest;
import com.givi.ecommerce.userservice.auth.dto.SignupRequest;
import com.givi.ecommerce.userservice.auth.dto.SignupResponse;
import com.givi.ecommerce.userservice.auth.service.AuthService;
import com.givi.ecommerce.userservice.entity.User;
import com.givi.ecommerce.userservice.enums.AuthProvider;
import com.givi.ecommerce.userservice.enums.Role;
import com.givi.ecommerce.userservice.enums.SellerStatus;
import com.givi.ecommerce.userservice.enums.UserStatus;
import com.givi.ecommerce.userservice.exception.DuplicateResourceException;
import com.givi.ecommerce.userservice.exception.InvalidCredentialsException;
import com.givi.ecommerce.userservice.repository.UserRepository;
import com.givi.ecommerce.userservice.security.jwt.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
    @Transactional
    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        )
                );

        Set<String> tokenRoles = user
                .getRoles()
                .stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        String accessToken = jwtUtil.generateAccessToken(
                user.getEmail(),
                tokenRoles
        );

        Set<String> responseRoles = user
                .getRoles()
                .stream()
                .map(role ->
                        role.name().replace("ROLE_", "")
                )
                .collect(Collectors.toSet());

        return new AuthResponse(
                accessToken,
                user.getId(),
                user.getEmail(),
                responseRoles,
                "Bearer"
        );
    }
}