package com.profitflow.core_app.common.config;

import com.profitflow.core_app.security.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.Executor;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableAsync
@RequiredArgsConstructor
public class AppConfig {

    private final MerchantRepository merchantRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> merchantRepository.findByEmail(username)
                                             .map(merchant -> User.withUsername(merchant.getEmail())
                                                                  .password(merchant.getPassword())
                                                                  .authorities("ROLE_USER")
                                                                  .build())
                                             .orElseThrow(() -> new UsernameNotFoundException("Merchant not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                return Optional.of(userDetails.getUsername());
            }
            return Optional.of(principal.toString());
        };
    }

    @Bean(name = "syncTaskExecutor")
    public Executor syncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("sync-");
        executor.initialize();
        return executor;
    }
}
