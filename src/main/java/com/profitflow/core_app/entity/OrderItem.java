package com.profitflow.core_app.entity;

import com.profitflow.core_app.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "platform_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal platformFee;

    @Column(name = "logistics_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal logisticsFee;

    @Column(name = "other_fees", nullable = false, precision = 10, scale = 2)
    private BigDecimal otherFees;

    @Column(name = "payout_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal payoutAmount;

    @PrePersist
    protected void onCreate() {
        if (quantity == null) quantity = 1;
        if (sellingPrice == null) sellingPrice = BigDecimal.ZERO;
        if (platformFee == null) platformFee = BigDecimal.ZERO;
        if (logisticsFee == null) logisticsFee = BigDecimal.ZERO;
        if (otherFees == null) otherFees = BigDecimal.ZERO;
        if (payoutAmount == null) payoutAmount = BigDecimal.ZERO;
    }
}