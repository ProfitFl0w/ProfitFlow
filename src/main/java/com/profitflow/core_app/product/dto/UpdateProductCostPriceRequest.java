package com.profitflow.core_app.product.dto;

import java.math.BigDecimal;

public record UpdateProductCostPriceRequest(
        BigDecimal costPrice,
        String currency
) {
}

