package com.profitflow.core_app.security.service.impl;

import com.profitflow.core_app.security.dto.AuthResponse;
import com.profitflow.core_app.security.dto.LoginRequest;
import com.profitflow.core_app.security.dto.RegisterRequest;
import com.profitflow.core_app.security.entity.Merchant;
import com.profitflow.core_app.security.repository.MerchantRepository;
import com.profitflow.core_app.security.service.AuthService;
import com.profitflow.core_app.exception.AppException;
import com.profitflow.core_app.exception.ErrorCode;
import com.profitflow.core_app.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (merchantRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.MERCHANT_ALREADY_EXISTS);
        }

        Merchant merchant = Merchant.builder()
                .companyName(request.getCompanyName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        merchantRepository.save(merchant);

        String jwtToken = jwtService.generateToken(merchant.getEmail());

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
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
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        Merchant merchant = merchantRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));

        String jwtToken = jwtService.generateToken(merchant.getEmail());

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}

