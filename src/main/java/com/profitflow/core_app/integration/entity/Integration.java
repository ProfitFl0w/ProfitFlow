package com.profitflow.core_app.integration.entity;

import com.profitflow.core_app.common.entity.SoftDeletableEntity;
import com.profitflow.core_app.security.EncryptedStringConverter;
import com.profitflow.core_app.security.entity.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "integrations",
        indexes = {
                @Index(name = "idx_integrations_merchant_id", columnList = "merchant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Integration extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Platform platform;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 6)
    private BigDecimal exchangeRate;
}
