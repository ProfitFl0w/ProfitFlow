package com.profitflow.core_app.analytic.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrdersAnalyticsResponse(int days, List<DailyOrderPoint> points) {
    public record DailyOrderPoint(LocalDate date, long ordersCount, BigDecimal revenue, String currency) {}
}