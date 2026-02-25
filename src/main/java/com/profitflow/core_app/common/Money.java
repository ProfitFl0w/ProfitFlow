package com.profitflow.core_app.common;

import com.profitflow.core_app.exception.AppException;
import com.profitflow.core_app.exception.ErrorCode;
import com.profitflow.core_app.common.util.CurrencyUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.Locale;

@Embeddable
@Builder
public class Money {

    @Column(name = "amount", nullable = false)
    private Long amount; // minor units

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currency; // ISO 4217

    protected Money() {
        // for JPA
    }

    public Money(Long amount, String currency) {
        if (amount == null) {
            throw new AppException(ErrorCode.INVALID_MONEY_AMOUNT);
        }
        if (currency == null || currency.isBlank()) {
            throw new AppException(ErrorCode.INVALID_CURRENCY_CODE);
        }

        String normalizedCurrency = currency.trim().toUpperCase(Locale.ROOT);
        try {
            // validate and resolve minor units eagerly
            CurrencyUtils.getMinorUnits(normalizedCurrency);
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INVALID_CURRENCY_CODE);
        }

        this.amount = amount;
        this.currency = normalizedCurrency;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        if (other == null) {
            return this;
        }
        if (!this.currency.equals(other.currency)) {
            throw new AppException(ErrorCode.CURRENCY_MISMATCH);
        }
        return new Money(this.amount + other.amount, this.currency);
    }

    public BigDecimal toDecimal() {
        int scale = CurrencyUtils.getMinorUnits(this.currency);
        return BigDecimal.valueOf(this.amount, scale);
    }

    public static Money of(BigDecimal decimalAmount, String currency) {
        if (decimalAmount == null) {
            throw new AppException(ErrorCode.INVALID_MONEY_AMOUNT);
        }
        String normalizedCurrency = currency.trim().toUpperCase(Locale.ROOT);
        int scale = CurrencyUtils.getMinorUnits(normalizedCurrency);
        long minorUnits = decimalAmount.scaleByPowerOfTen(scale).longValueExact();
        return new Money(minorUnits, normalizedCurrency);
    }
}