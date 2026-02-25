package com.profitflow.core_app.integration.dto;

import java.util.UUID;

public record UploadAcceptedResponse(
        UUID integrationId,
        String fileName,
        long fileSize,
        String status
) {
}