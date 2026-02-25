package com.profitflow.core_app.repository;

import com.profitflow.core_app.entity.order.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByMerchantId(UUID merchantId);

    @Query("SELECT p FROM Product p WHERE p.merchant.id = :merchantId AND p.isDeleted = false")
    List<Product> findAllActiveByMerchantId(@Param("merchantId") UUID merchantId);

    Optional<Product> findByMerchantIdAndSku(UUID merchantId, String sku);

    @Query("SELECT p FROM Product p WHERE p.merchant.id = :merchantId AND p.sku = :sku AND p.isDeleted = false")
    Optional<Product> findActiveByMerchantIdAndSku(
            @Param("merchantId") UUID merchantId,
            @Param("sku") String sku
    );
}
