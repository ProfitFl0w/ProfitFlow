package com.profitflow.core_app.controller;

import com.profitflow.core_app.entity.integration.Platform;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integrations")
public class IntegrationsController {

    @GetMapping
    public ResponseEntity<List<IntegrationStub>> listIntegrations() {
        return ResponseEntity.ok(List.of(
                new IntegrationStub(
                        UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        Platform.KASPI,
                        "Kaspi Main Store",
                        true,
                        OffsetDateTime.now().minusHours(2)
                ),
                new IntegrationStub(
                        UUID.fromString("22222222-2222-2222-2222-222222222222"),
                        Platform.OZON,
                        "Ozon Seller Cabinet",
                        false,
                        null
                )
        ));
    }

    @PostMapping
    public ResponseEntity<IntegrationStub> createIntegration(@RequestBody CreateIntegrationRequest request) {
        IntegrationStub response = new IntegrationStub(
                UUID.randomUUID(),
                request.platform(),
                request.name(),
                true,
                null
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/sync")
    public ResponseEntity<SyncStartResponse> startSync(@PathVariable UUID id) {
        return ResponseEntity.accepted().body(new SyncStartResponse(
                UUID.randomUUID(),
                id,
                "IN_PROGRESS",
                "Stub sync job started"
        ));
    }

    @PostMapping(path = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadAcceptedResponse> uploadReport(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.accepted().body(new UploadAcceptedResponse(
                id,
                file.getOriginalFilename(),
                file.getSize(),
                "accepted"
        ));
    }

    public record CreateIntegrationRequest(
            Platform platform,
            @NotBlank String name,
            String apiKeyMaskedHint
    ) {
    }

    public record IntegrationStub(
            UUID id,
            Platform platform,
            String name,
            boolean active,
            OffsetDateTime lastSyncedAt
    ) {
    }

    public record SyncStartResponse(
            UUID jobId,
            UUID integrationId,
            String status,
            String message
    ) {
    }

    public record UploadAcceptedResponse(
            UUID integrationId,
            String fileName,
            long fileSize,
            String status
    ) {
    }
}
