package com.profitflow.core_app.entity.order;

import com.profitflow.core_app.entity.Merchant;
import com.profitflow.core_app.entity.basic.SoftDeletableEntity;
import com.profitflow.core_app.entity.basic.Money;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products", uniqueConstraints = {@UniqueConstraint(columnNames = {"merchant_id", "sku"})}) //а зачем так делать, нельзя разве саму колонку unique = true делать?
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