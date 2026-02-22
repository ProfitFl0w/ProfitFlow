package com.profitflow.core_app.repository;

import com.profitflow.core_app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByMerchantId(UUID merchantId);
    Optional<Product> findByMerchantIdAndSku(UUID merchantId, String sku);
}