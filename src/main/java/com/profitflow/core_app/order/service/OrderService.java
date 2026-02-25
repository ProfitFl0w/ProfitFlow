package com.profitflow.core_app.order.service;


import com.profitflow.core_app.order.entity.Order;
import com.profitflow.core_app.order.entity.OrderItem;
import java.util.List;
import java.util.UUID;

public interface OrderService {

    /**
     * Persists a normalized order and its items. Idempotent — skips existing orders
     * identified by (integrationId, externalId).
     */
    Order saveOrSkip(Order order, List<OrderItem> items);

    List<Order> findByIntegration(UUID integrationId);
}
