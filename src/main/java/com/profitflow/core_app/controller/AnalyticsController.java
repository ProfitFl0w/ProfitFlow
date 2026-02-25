package com.profitflow.core_app.controller;

import com.profitflow.core_app.dto.analytics.DashboardResponse;
import com.profitflow.core_app.dto.analytics.OrdersAnalyticsResponse;
import com.profitflow.core_app.dto.analytics.ProfitByProductResponse;
import com.profitflow.core_app.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Profit and revenue analytics for the authenticated merchant")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard overview",
               description = "Returns total orders, revenue, net profit and products missing cost price")
    public ResponseEntity<DashboardResponse> dashboard(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(analyticsService.getDashboard(userDetails.getUsername()));
    }

    @GetMapping("/orders")
    @Operation(summary = "Orders trend",
               description = "Returns daily orders count and revenue for the last N days (1-365)")
    public ResponseEntity<OrdersAnalyticsResponse> orders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "7") int days
    ) {
        return ResponseEntity.ok(analyticsService.getOrdersAnalytics(userDetails.getUsername(), days));
    }

    @GetMapping("/profit-by-product")
    @Operation(summary = "Profit breakdown by product",
               description = "Returns net profit, gross revenue and margin per product SKU, sorted by profit descending")
    public ResponseEntity<List<ProfitByProductResponse>> profitByProduct(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(analyticsService.getProfitByProduct(userDetails.getUsername()));
    }
}
