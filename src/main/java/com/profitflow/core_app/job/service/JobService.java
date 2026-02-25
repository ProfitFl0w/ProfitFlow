package com.profitflow.core_app.job.service;

import com.profitflow.core_app.job.entity.Job;

import java.util.UUID;

public interface JobService {
    /**
     * Creates a sync job record and launches background processing asynchronously.
     * Returns the created job immediately — poll {@link #getJob(UUID)} for status.
     */
    Job startSync(UUID integrationId, String merchantEmail);
    Job getJob(UUID jobId);
}