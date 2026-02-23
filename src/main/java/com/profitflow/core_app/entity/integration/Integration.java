package com.profitflow.core_app.entity.integration;

import com.profitflow.core_app.entity.Merchant;
import com.profitflow.core_app.entity.basic.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "integrations")
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

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "shop_name", nullable = false)
    private String shopName; // че за шоп нейм? Имя комапний что ли? Или просто стринг формат Kaspi, Ozon -а?

    @Column(name = "currency", nullable = false, length = 3)
    private String currency; // А это не должен быть фиксированный енам или там че то такое а не просто стринг?

    @Column(name = "exchange_rate", nullable = false, precision = 18, scale = 6)
    private BigDecimal exchangeRate; // Это что такое?
}