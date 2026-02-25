package com.profitflow.core_app.product.entity;

import com.profitflow.core_app.common.Money;
import com.profitflow.core_app.common.entity.SoftDeletableEntity;
import com.profitflow.core_app.security.entity.Merchant;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uc_products_merchant_id_sku",
                        columnNames = {"merchant_id", "sku"}
                )
        },
        indexes = {
                @Index(name = "idx_products_merchant_id", columnList = "merchant_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "cost_price_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "cost_price_currency_code", nullable = false, length = 3))
    })
    private Money costPrice;
}

