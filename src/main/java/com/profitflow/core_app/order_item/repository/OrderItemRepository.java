package com.profitflow.core_app.order_item.repository;

import com.profitflow.core_app.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findAllByOrderId(UUID orderId);

    @Query("""
            SELECT oi FROM OrderItem oi
            JOIN FETCH oi.product p
            JOIN FETCH oi.order o
            JOIN FETCH o.integration i
            WHERE i.merchant.id = :merchantId
            AND oi.isDeleted = false
            AND o.isDeleted = false
            AND i.isDeleted = false
            """)
    List<OrderItem> findAllActiveByMerchantId(@Param("merchantId") UUID merchantId);

    @Query("""
            SELECT oi FROM OrderItem oi
            JOIN FETCH oi.product p
            JOIN FETCH oi.order o
            JOIN FETCH o.integration i
            WHERE i.merchant.id = :merchantId
            AND o.orderDate >= :startDate
            AND oi.isDeleted = false
            AND o.isDeleted = false
            AND i.isDeleted = false
            ORDER BY o.orderDate ASC
            """)
    List<OrderItem> findAllActiveByMerchantIdAndOrderDateAfter(
            @Param("merchantId") UUID merchantId,
            @Param("startDate") OffsetDateTime startDate
    );
}
