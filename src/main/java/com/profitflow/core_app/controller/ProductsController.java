package com.profitflow.core_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductsController {

    @GetMapping
    public ResponseEntity<List<ProductStub>> listProducts() {
        return ResponseEntity.ok(List.of(
                new ProductStub(
                        UUID.fromString("33333333-3333-3333-3333-333333333333"),
                        "SKU-001",
                        "Wireless Mouse",
                        new BigDecimal("3500.00"),
                        new BigDecimal("5900.00"),
                        "KZT"
                ),
                new ProductStub(
                        UUID.fromString("44444444-4444-4444-4444-444444444444"),
                        "SKU-002",
                        "Mechanical Keyboard",
                        null,
                        new BigDecimal("12900.00"),
                        "KZT"
                )
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductStub> updateCostPrice(
            @PathVariable UUID id,
            @RequestBody UpdateProductCostPriceRequest request
    ) {
        return ResponseEntity.ok(new ProductStub(
                id,
                "SKU-STUB",
                "Updated Product (stub)",
                request.costPrice(),
                new BigDecimal("9900.00"),
                request.currency() == null ? "KZT" : request.currency()
        ));
    }

    public record UpdateProductCostPriceRequest(
            BigDecimal costPrice,
            String currency
    ) {
    }

    public record ProductStub(
            UUID id,
            String sku,
            String name,
            BigDecimal costPrice,
            BigDecimal salePrice,
            String currency
    ) {
    }
}
