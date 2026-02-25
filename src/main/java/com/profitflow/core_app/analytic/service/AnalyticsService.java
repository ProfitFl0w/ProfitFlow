package com.profitflow.core_app.analytic.service;

import com.profitflow.core_app.analytic.dto.DashboardResponse;
import com.profitflow.core_app.analytic.dto.OrdersAnalyticsResponse;
import com.profitflow.core_app.analytic.dto.ProfitByProductResponse;

import java.util.List;

public interface AnalyticsService {

    DashboardResponse getDashboard(String merchantEmail);

    OrdersAnalyticsResponse getOrdersAnalytics(String merchantEmail, int days);

    List<ProfitByProductResponse> getProfitByProduct(String merchantEmail);
}
