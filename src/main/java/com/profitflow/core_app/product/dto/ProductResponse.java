package com.profitflow.core_app.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        BigDecimal costPrice,
        BigDecimal salePrice,
        String currency
) {
}

