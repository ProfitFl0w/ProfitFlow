package com.profitflow.core_app.repository;

import com.profitflow.core_app.entity.integration.Integration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, UUID> {
    List<Integration> findAllByMerchantId(UUID merchantId);
}