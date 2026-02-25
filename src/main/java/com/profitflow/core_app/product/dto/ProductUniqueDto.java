package com.profitflow.core_app.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductUniqueDto(
        UUID id,
        String sku,
        String name,
        BigDecimal costPriceAmount,
        String costPriceCurrency
) {
}

