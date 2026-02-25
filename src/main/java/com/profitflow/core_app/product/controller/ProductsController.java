package com.profitflow.core_app.product.controller;

import com.profitflow.core_app.product.dto.ProductResponse;
import com.profitflow.core_app.product.dto.UpdateProductCostPriceRequest;
import com.profitflow.core_app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> listProducts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ProductResponse> products = productService.listByMerchant(userDetails.getUsername());
        return ResponseEntity
                .status(200)
                .body(products);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> updateCostPrice(
            @PathVariable UUID id,
            @RequestBody UpdateProductCostPriceRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ProductResponse updated = productService.updateCostPrice(
                id,
                userDetails.getUsername(),
                request.costPrice(),
                request.currency()
        );

        return ResponseEntity
                .status(200)
                .body(updated);
    }
}
