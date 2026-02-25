package com.profitflow.core_app.service;

import com.profitflow.core_app.dto.auth.AuthResponse;
import com.profitflow.core_app.dto.auth.LoginRequest;
import com.profitflow.core_app.dto.auth.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    public AuthResponse login(LoginRequest request);
}