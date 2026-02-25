package com.profitflow.core_app.datalayer.model;

import java.math.BigDecimal;
import java.util.Map;

public record ProfitResult(
        BigDecimal grossRevenue,
        BigDecimal totalFees,
        BigDecimal costPrice,
        BigDecimal netProfit,
        BigDecimal marginPercent,
        Map<FeeType, BigDecimal> feeBreakdown
) {}