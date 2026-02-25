package com.profitflow.core_app.integration.repository;

import com.profitflow.core_app.integration.entity.Integration;
import com.profitflow.core_app.integration.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, UUID> {

    List<Integration> findAllByMerchantId(UUID merchantId);

    @Query("SELECT i FROM Integration i WHERE i.merchant.id = :merchantId AND i.isDeleted = false")
    List<Integration> findAllActiveByMerchantId(@Param("merchantId") UUID merchantId);

    @Query("SELECT i FROM Integration i WHERE i.merchant.id = :merchantId AND i.platform = :platform AND i.isDeleted = false")
    Optional<Integration> findActiveByMerchantIdAndPlatform(
            @Param("merchantId") UUID merchantId,
            @Param("platform") Platform platform
    );

    @Query("SELECT i FROM Integration i WHERE i.id = :id AND i.merchant.id = :merchantId AND i.isDeleted = false")
    Optional<Integration> findActiveByIdAndMerchantId(
            @Param("id") UUID id,
            @Param("merchantId") UUID merchantId
    );
}
