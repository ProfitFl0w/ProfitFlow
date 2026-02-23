package com.profitflow.core_app.entity.order;

import com.profitflow.core_app.entity.basic.Money;
import com.profitflow.core_app.entity.basic.SoftDeletableEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "order_items",
        indexes = {
                @Index(name = "idx_order_items_order_id", columnList = "order_id"),
                @Index(name = "idx_order_items_product_id", columnList = "product_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends SoftDeletableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "selling_price_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "selling_price_currency_code", nullable = false, length = 3))
    })
    private Money sellingPrice;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "platform_fee_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "platform_fee_currency_code", nullable = false, length = 3))
    })
    private Money platformFee;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "logistics_fee_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "logistics_fee_currency_code", nullable = false, length = 3))
    })
    private Money logisticsFee;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "other_fees_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "other_fees_currency_code", nullable = false, length = 3))
    })
    private Money otherFees;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "payout_amount_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "payout_amount_currency_code", nullable = false, length = 3))
    })
    private Money payoutAmount;
}