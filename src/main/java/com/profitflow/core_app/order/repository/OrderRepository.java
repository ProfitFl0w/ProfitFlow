package com.profitflow.core_app.order.repository;

import com.profitflow.core_app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByIntegrationIdAndExternalId(UUID integrationId, String externalId);

    List<Order> findAllByIntegrationIdAndOrderDateBetween(
            UUID integrationId,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    );

    @Query("SELECT COUNT(o) FROM Order o " +
           "JOIN o.integration i " +
           "WHERE i.merchant.id = :merchantId " +
           "AND o.isDeleted = false " +
           "AND i.isDeleted = false")
    long countActiveByMerchantId(@Param("merchantId") UUID merchantId);

    @Query("SELECT o FROM Order o " +
           "JOIN FETCH o.integration i " +
           "WHERE i.merchant.id = :merchantId " +
           "AND o.orderDate >= :startDate " +
           "AND o.isDeleted = false " +
           "AND i.isDeleted = false " +
           "ORDER BY o.orderDate ASC")
    List<Order> findAllActiveByMerchantIdAndOrderDateAfter(
            @Param("merchantId") UUID merchantId,
            @Param("startDate") OffsetDateTime startDate
    );
}
