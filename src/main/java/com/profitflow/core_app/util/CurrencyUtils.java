package com.profitflow.core_app.util;

import java.util.Currency;

public final class CurrencyUtils {

    private CurrencyUtils() {
    }

    public static int getMinorUnits(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        return currency.getDefaultFractionDigits();
    }
}

