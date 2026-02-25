package com.profitflow.core_app.integration.dto;

import java.util.UUID;

public record SyncStartResponse(
        UUID jobId,
        UUID integrationId,
        String status,
        String message
) {
}