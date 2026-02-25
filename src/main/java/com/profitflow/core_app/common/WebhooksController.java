package com.profitflow.core_app.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhooksController {

    @PostMapping("/{platform}")
    public ResponseEntity<WebhookAckResponse> handleWebhook(
            @PathVariable String platform,
            @RequestHeader Map<String, String> headers,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        return ResponseEntity.accepted().body(new WebhookAckResponse(
                platform,
                headers.getOrDefault("x-signature", headers.getOrDefault("X-Signature", "missing")),
                payload == null ? 0 : payload.size(),
                OffsetDateTime.now(),
                "accepted (stub)"
        ));
    }

    public record WebhookAckResponse(
            String platform,
            String signature,
            int payloadFields,
            OffsetDateTime receivedAt,
            String status
    ) {
    }
}
