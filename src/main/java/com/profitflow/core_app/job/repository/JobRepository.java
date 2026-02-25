package com.profitflow.core_app.job.repository;

import com.profitflow.core_app.job.entity.Job;
import com.profitflow.core_app.job.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findAllByIntegrationId(UUID integrationId);

    Optional<Job> findTopByIntegrationIdOrderByCreatedAtDesc(UUID integrationId);

    List<Job> findAllByStatus(JobStatus status);
}
