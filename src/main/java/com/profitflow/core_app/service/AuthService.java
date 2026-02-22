package com.profitflow.core_app.service;

import com.profitflow.core_app.dto.auth.AuthResponse;
import com.profitflow.core_app.dto.auth.LoginRequest;
import com.profitflow.core_app.dto.auth.RegisterRequest;
import com.profitflow.core_app.entity.Merchant;
import com.profitflow.core_app.exception.AppException;
import com.profitflow.core_app.exception.ErrorCodes;
import com.profitflow.core_app.repository.MerchantRepository;
import com.profitflow.core_app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (merchantRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCodes.MERCHANT_ALREADY_EXISTS);
        }

        var merchant = Merchant.builder()
                               .companyName(request.getCompanyName())
                               .email(request.getEmail())
                               .password(passwordEncoder.encode(request.getPassword())) // Обязательно хешируем пароль!
                               .build();

        merchantRepository.save(merchant);

        var jwtToken = jwtService.generateToken(merchant);

        return AuthResponse.builder()
                           .token(jwtToken)
                           .build();
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new AppException(ErrorCodes.INVALID_CREDENTIALS);
        }

        var merchant = merchantRepository.findByEmail(request.getEmail())
                                         .orElseThrow(() -> new AppException(ErrorCodes.MERCHANT_NOT_FOUND));

        var jwtToken = jwtService.generateToken(merchant);

        return AuthResponse.builder()
                           .token(jwtToken)
                           .build();
    }
}