package com.profitflow.core_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(new DashboardResponse(
                124,
                new BigDecimal("1542000.50"),
                new BigDecimal("286700.20"),
                7,
                "Stub dashboard data"
        ));
    }

    @GetMapping("/orders")
    public ResponseEntity<OrdersAnalyticsResponse> orders(
            @RequestParam(defaultValue = "7") int days
    ) {
        int clampedDays = Math.max(1, days);
        return ResponseEntity.ok(new OrdersAnalyticsResponse(
                clampedDays,
                List.of(
                        new OrdersPoint("2026-02-20", 18, new BigDecimal("181000.00")),
                        new OrdersPoint("2026-02-21", 25, new BigDecimal("263500.00")),
                        new OrdersPoint("2026-02-22", 21, new BigDecimal("219900.00"))
                )
        ));
    }

    @GetMapping("/profit-by-product")
    public ResponseEntity<List<ProfitByProductRow>> profitByProduct() {
        return ResponseEntity.ok(List.of(
                new ProfitByProductRow("SKU-001", "Wireless Mouse", new BigDecimal("54000.00"), new BigDecimal("12200.00")),
                new ProfitByProductRow("SKU-002", "Mechanical Keyboard", new BigDecimal("87000.00"), new BigDecimal("18400.00"))
        ));
    }

    public record DashboardResponse(
            int totalOrders,
            BigDecimal revenue,
            BigDecimal profit,
            int productsWithoutCostPrice,
            String note
    ) {
    }

    public record OrdersAnalyticsResponse(
            int days,
            List<OrdersPoint> points
    ) {
    }

    public record OrdersPoint(
            String date,
            int ordersCount,
            BigDecimal revenue
    ) {
    }

    public record ProfitByProductRow(
            String sku,
            String productName,
            BigDecimal revenue,
            BigDecimal profit
    ) {
    }
}
