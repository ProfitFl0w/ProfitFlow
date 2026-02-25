package com.profitflow.core_app.analytic.service.impl;

import com.profitflow.core_app.analytic.dto.DashboardResponse;
import com.profitflow.core_app.analytic.dto.OrdersAnalyticsResponse;
import com.profitflow.core_app.analytic.dto.OrdersAnalyticsResponse.DailyOrderPoint;
import com.profitflow.core_app.analytic.dto.ProfitByProductResponse;
import com.profitflow.core_app.analytic.service.AnalyticsService;
import com.profitflow.core_app.exception.AppException;
import com.profitflow.core_app.exception.ErrorCode;
import com.profitflow.core_app.order.entity.Order;
import com.profitflow.core_app.order.entity.OrderItem;
import com.profitflow.core_app.order.entity.OrderItemAdjustment;
import com.profitflow.core_app.order.repository.OrderRepository;
import com.profitflow.core_app.order_item.repository.OrderItemAdjustmentRepository;
import com.profitflow.core_app.order_item.repository.OrderItemRepository;
import com.profitflow.core_app.product.entity.Product;
import com.profitflow.core_app.product.repository.ProductRepository;
import com.profitflow.core_app.security.entity.Merchant;
import com.profitflow.core_app.security.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final String FALLBACK_CURRENCY = "KZT";

    private final MerchantRepository merchantRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemAdjustmentRepository adjustmentRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public DashboardResponse getDashboard(String merchantEmail) {
        Merchant merchant = requireMerchant(merchantEmail);
        UUID merchantId = merchant.getId();

        long totalOrders = orderRepository.countActiveByMerchantId(merchantId);
        List<OrderItem> allItems = orderItemRepository.findAllActiveByMerchantId(merchantId);
        List<OrderItemAdjustment> allAdjustments = adjustmentRepository.findAllActiveByMerchantId(merchantId);

        Map<UUID, List<OrderItemAdjustment>> adjByItemId = allAdjustments.stream()
                .collect(Collectors.groupingBy(adj -> adj.getOrderItem().getId()));

        BigDecimal totalRevenue = BigDecimal.ZERO; // Все операций связанные с деньгами вообще должны идти через Money
        BigDecimal totalPayout = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalAdjustments = BigDecimal.ZERO;
        String currency = FALLBACK_CURRENCY;

        for (OrderItem item : allItems) {
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            totalRevenue = totalRevenue.add(item.getSellingPrice().toDecimal().multiply(quantity));
            totalPayout = totalPayout.add(item.getPayoutAmount().toDecimal());
            currency = item.getSellingPrice().getCurrency();

            BigDecimal costPerUnit = item.getProduct().getCostPrice() != null
                    ? item.getProduct().getCostPrice().toDecimal()
                    : BigDecimal.ZERO;
            totalCost = totalCost.add(costPerUnit.multiply(quantity));
        }

        for (OrderItemAdjustment adj : allAdjustments) {
            totalAdjustments = totalAdjustments.add(adj.getAmount().toDecimal());
        }

        // netProfit = payout_amount + adjustments - cost_price × quantity  (per rulesForAI.context formula)
        BigDecimal totalProfit = totalPayout.add(totalAdjustments).subtract(totalCost);

        List<Product> allProducts = productRepository.findAllActiveByMerchantId(merchantId);
        int productsWithoutCostPrice = (int) allProducts.stream()
                .filter(p -> p.getCostPrice() == null || p.getCostPrice().getAmount() == 0)
                .count();

        return new DashboardResponse(
                totalOrders,
                totalRevenue.setScale(2, RoundingMode.HALF_UP),
                currency,
                totalProfit.setScale(2, RoundingMode.HALF_UP),
                currency,
                productsWithoutCostPrice
        );
    }

    @Override
    public OrdersAnalyticsResponse getOrdersAnalytics(String merchantEmail, int days) {
        Merchant merchant = requireMerchant(merchantEmail);
        UUID merchantId = merchant.getId();

        int clampedDays = Math.max(1, Math.min(days, 365));
        OffsetDateTime startDate = OffsetDateTime.now(ZoneOffset.UTC).minusDays(clampedDays);

        List<Order> recentOrders = orderRepository.findAllActiveByMerchantIdAndOrderDateAfter(merchantId, startDate);
        List<OrderItem> recentItems = orderItemRepository.findAllActiveByMerchantIdAndOrderDateAfter(merchantId, startDate);

        Map<LocalDate, Long> orderCountByDay = recentOrders.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOrderDate().toLocalDate(),
                        Collectors.counting()
                ));

        Map<LocalDate, List<OrderItem>> itemsByDay = recentItems.stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getOrderDate().toLocalDate()));

        // Build ordered date range from startDate to today (inclusive)
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<DailyOrderPoint> points = startDate.toLocalDate()
                .datesUntil(today.plusDays(1))
                .map(date -> {
                    long count = orderCountByDay.getOrDefault(date, 0L);
                    List<OrderItem> dayItems = itemsByDay.getOrDefault(date, List.of());

                    BigDecimal revenue = dayItems.stream()
                            .map(item -> item.getSellingPrice().toDecimal()
                                    .multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    String currency = dayItems.isEmpty()
                            ? FALLBACK_CURRENCY
                            : dayItems.get(0).getSellingPrice().getCurrency();

                    return new DailyOrderPoint(date, count, revenue.setScale(2, RoundingMode.HALF_UP), currency);
                })
                .toList();

        return new OrdersAnalyticsResponse(clampedDays, points);
    }

    @Override
    public List<ProfitByProductResponse> getProfitByProduct(String merchantEmail) {
        Merchant merchant = requireMerchant(merchantEmail);
        UUID merchantId = merchant.getId();

        List<OrderItem> allItems = orderItemRepository.findAllActiveByMerchantId(merchantId);
        List<OrderItemAdjustment> allAdjustments = adjustmentRepository.findAllActiveByMerchantId(merchantId);

        Map<UUID, List<OrderItemAdjustment>> adjByItemId = allAdjustments.stream()
                .collect(Collectors.groupingBy(adj -> adj.getOrderItem().getId()));

        // Group order items by product
        Map<UUID, List<OrderItem>> itemsByProductId = allItems.stream()
                .collect(Collectors.groupingBy(item -> item.getProduct().getId()));

        List<ProfitByProductResponse> result = new ArrayList<>();

        for (Map.Entry<UUID, List<OrderItem>> entry : itemsByProductId.entrySet()) {
            UUID productId = entry.getKey();
            List<OrderItem> items = entry.getValue();
            Product product = items.get(0).getProduct();

            BigDecimal grossRevenue = BigDecimal.ZERO;
            BigDecimal totalPayout = BigDecimal.ZERO;
            BigDecimal totalAdjustments = BigDecimal.ZERO;
            BigDecimal totalCost = BigDecimal.ZERO;

            BigDecimal costPerUnit = product.getCostPrice() != null
                    ? product.getCostPrice().toDecimal()
                    : BigDecimal.ZERO;

            String currency = product.getCostPrice() != null
                    ? product.getCostPrice().getCurrency()
                    : items.get(0).getSellingPrice().getCurrency();

            for (OrderItem item : items) {
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                grossRevenue = grossRevenue.add(item.getSellingPrice().toDecimal().multiply(quantity));
                totalPayout = totalPayout.add(item.getPayoutAmount().toDecimal());
                totalCost = totalCost.add(costPerUnit.multiply(quantity));

                List<OrderItemAdjustment> itemAdj = adjByItemId.getOrDefault(item.getId(), List.of());
                for (OrderItemAdjustment adj : itemAdj) {
                    totalAdjustments = totalAdjustments.add(adj.getAmount().toDecimal());
                }
            }

            // totalFees = what the marketplace deducted (selling price - payout)
            BigDecimal totalFees = grossRevenue.subtract(totalPayout);
            // netProfit = payout + adjustments - cost (per rulesForAI.context formula)
            BigDecimal netProfit = totalPayout.add(totalAdjustments).subtract(totalCost);

            BigDecimal marginPercent;
            if (grossRevenue.compareTo(BigDecimal.ZERO) == 0) {
                marginPercent = BigDecimal.ZERO;
            } else {
                marginPercent = netProfit
                        .divide(grossRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            result.add(new ProfitByProductResponse(
                    productId,
                    product.getSku(),
                    product.getName(),
                    grossRevenue.setScale(2, RoundingMode.HALF_UP),
                    totalFees.setScale(2, RoundingMode.HALF_UP),
                    netProfit.setScale(2, RoundingMode.HALF_UP),
                    marginPercent.setScale(2, RoundingMode.HALF_UP),
                    items.size(),
                    currency
            ));
        }

        // Sort by net profit descending — most profitable products first
        result.sort((a, b) -> b.netProfit().compareTo(a.netProfit()));
        return result;
    }

    private Merchant requireMerchant(String email) {
        return merchantRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));
    }
}
