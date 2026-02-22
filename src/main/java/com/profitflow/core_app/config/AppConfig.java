package com.profitflow.core_app.config;

import com.profitflow.core_app.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final MerchantRepository merchantRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> merchantRepository.findByEmail(username)
                                             .orElseThrow(() -> new UsernameNotFoundException("Merchant not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}