package com.profitflow.core_app.analytic.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProfitByProductResponse(
        UUID productId,
        String sku,
        String productName,
        BigDecimal grossRevenue,
        BigDecimal totalFees,
        BigDecimal netProfit,
        BigDecimal marginPercent,
        int totalOrderItems,
        String currency
) {}
