package com.profitflow.core_app.order_item.repository;

import com.profitflow.core_app.order.entity.OrderItemAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemAdjustmentRepository extends JpaRepository<OrderItemAdjustment, UUID> {

    @Query("""
            SELECT adj FROM OrderItemAdjustment adj
            JOIN adj.orderItem oi
            JOIN oi.order o
            JOIN o.integration i
            WHERE i.merchant.id = :merchantId
            AND adj.isDeleted = false
            AND oi.isDeleted = false
            AND o.isDeleted = false
            AND i.isDeleted = false
            """)
    List<OrderItemAdjustment> findAllActiveByMerchantId(@Param("merchantId") UUID merchantId);
}
