package com.profitflow.core_app.service.impl;

import com.profitflow.core_app.dto.auth.AuthResponse;
import com.profitflow.core_app.dto.auth.LoginRequest;
import com.profitflow.core_app.dto.auth.RegisterRequest;
import com.profitflow.core_app.entity.Merchant;
import com.profitflow.core_app.exception.AppException;
import com.profitflow.core_app.exception.ErrorCode;
import com.profitflow.core_app.repository.MerchantRepository;
import com.profitflow.core_app.security.JwtService;
import com.profitflow.core_app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
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
