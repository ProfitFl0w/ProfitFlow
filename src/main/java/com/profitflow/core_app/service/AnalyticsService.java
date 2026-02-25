package com.profitflow.core_app.service;

import com.profitflow.core_app.dto.analytics.DashboardResponse;
import com.profitflow.core_app.dto.analytics.OrdersAnalyticsResponse;
import com.profitflow.core_app.dto.analytics.ProfitByProductResponse;

import java.util.List;

public interface AnalyticsService {

    DashboardResponse getDashboard(String merchantEmail);

    OrdersAnalyticsResponse getOrdersAnalytics(String merchantEmail, int days);

    List<ProfitByProductResponse> getProfitByProduct(String merchantEmail);
}
