package com.profitflow.core_app.integration.dto;

import com.profitflow.core_app.integration.entity.Platform;

import java.time.OffsetDateTime;
import java.util.UUID;

public record IntegrationStub(
        UUID id,
        Platform platform,
        String name,
        boolean active,
        OffsetDateTime lastSyncedAt
) {
}