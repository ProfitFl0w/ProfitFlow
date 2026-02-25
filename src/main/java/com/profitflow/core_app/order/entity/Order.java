package com.profitflow.core_app.order.entity;

import com.profitflow.core_app.common.entity.SoftDeletableEntity;
import com.profitflow.core_app.integration.entity.Integration;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uc_orders_integration_id_external_id",
                        columnNames = {"integration_id", "external_id"}
                )
        },
        indexes = {
                @Index(name = "idx_orders_integration_id", columnList = "integration_id"),
                @Index(name = "idx_orders_order_date", columnList = "order_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "integration_id", nullable = false)
    private Integration integration;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(name = "order_date", nullable = false)
    private OffsetDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "raw_data", columnDefinition = "jsonb")
    private String rawData;
}

