package com.profitflow.core_app.repository;

import com.profitflow.core_app.entity.SyncJob;
import com.profitflow.core_app.entity.SyncJobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SyncJobRepository extends JpaRepository<SyncJob, UUID> {

    List<SyncJob> findAllByIntegrationId(UUID integrationId);

    Optional<SyncJob> findTopByIntegrationIdOrderByCreatedAtDesc(UUID integrationId);

    List<SyncJob> findAllByStatus(SyncJobStatus status);
}
