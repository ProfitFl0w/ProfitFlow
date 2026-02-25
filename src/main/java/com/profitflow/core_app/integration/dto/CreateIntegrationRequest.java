package com.profitflow.core_app.integration.dto;

import com.profitflow.core_app.integration.entity.Platform;
import jakarta.validation.constraints.NotBlank;

public record CreateIntegrationRequest(
        Platform platform,
        @NotBlank String name,
        String apiKeyMaskedHint
) {
}