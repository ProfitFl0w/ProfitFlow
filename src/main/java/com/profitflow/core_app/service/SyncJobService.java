package com.profitflow.core_app.service;

import com.profitflow.core_app.entity.SyncJob;

import java.util.UUID;

public interface SyncJobService {

    /**
     * Creates a sync job record and launches background processing asynchronously.
     * Returns the created job immediately — poll {@link #getJob(UUID)} for status.
     */
    SyncJob startSync(UUID integrationId, String merchantEmail);

    SyncJob getJob(UUID jobId);
}
