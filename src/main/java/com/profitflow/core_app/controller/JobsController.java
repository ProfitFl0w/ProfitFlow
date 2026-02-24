package com.profitflow.core_app.controller;

import com.profitflow.core_app.entity.SyncJobStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobsController {

    @GetMapping("/{id}")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(new JobStatusResponse(
                id,
                SyncJobStatus.IN_PROGRESS,
                45,
                "Collecting orders from OZON (stub)",
                OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(20),
                null
        ));
    }

    public record JobStatusResponse(
            UUID jobId,
            SyncJobStatus status,
            int progressPercent,
            String message,
            OffsetDateTime startedAt,
            OffsetDateTime finishedAt
    ) {
    }
}
