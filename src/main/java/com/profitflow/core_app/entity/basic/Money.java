package com.profitflow.core_app.entity.basic;

import com.profitflow.core_app.exception.AppException;
import com.profitflow.core_app.exception.ErrorCode;
import com.profitflow.core_app.util.CurrencyUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
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
        this.amount = amount;
        this.currency = currency;
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
}

