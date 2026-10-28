package com.givi.ecommerce.userservice.auth.controller;

import com.givi.ecommerce.userservice.auth.dto.AuthResponse;
import com.givi.ecommerce.userservice.auth.dto.LoginRequest;
import com.givi.ecommerce.userservice.auth.dto.SignupRequest;
import com.givi.ecommerce.userservice.auth.dto.SignupResponse;
import com.givi.ecommerce.userservice.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<SignupResponse> register(
            @Valid @RequestBody SignupRequest request
    ) {
        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }
}