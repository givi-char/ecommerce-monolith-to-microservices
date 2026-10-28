package com.givi.ecommerce.userservice.auth.service;

import com.givi.ecommerce.userservice.auth.dto.AuthResponse;
import com.givi.ecommerce.userservice.auth.dto.LoginRequest;
import com.givi.ecommerce.userservice.auth.dto.SignupRequest;
import com.givi.ecommerce.userservice.auth.dto.SignupResponse;

public interface AuthService {

    SignupResponse signup(SignupRequest request);

    AuthResponse login(LoginRequest request);
}