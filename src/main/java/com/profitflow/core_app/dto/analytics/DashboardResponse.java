package com.profitflow.core_app.dto.analytics;

import java.math.BigDecimal;

public record DashboardResponse(
        long totalOrders,
        BigDecimal totalRevenue,
        String revenueCurrency,
        BigDecimal totalProfit,
        String profitCurrency,
        int productsWithoutCostPrice
) {}
