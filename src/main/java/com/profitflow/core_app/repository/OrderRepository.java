package com.profitflow.core_app.repository;

import com.profitflow.core_app.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByIntegrationIdAndExternalId(UUID integrationId, String externalId);
    List<Order> findAllByIntegrationIdAndOrderDateBetween(UUID integrationId, OffsetDateTime startDate, OffsetDateTime endDate);
}