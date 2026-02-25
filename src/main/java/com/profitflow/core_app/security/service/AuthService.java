package com.profitflow.core_app.security.service;

import com.profitflow.core_app.security.dto.AuthResponse;
import com.profitflow.core_app.security.dto.LoginRequest;
import com.profitflow.core_app.security.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}

