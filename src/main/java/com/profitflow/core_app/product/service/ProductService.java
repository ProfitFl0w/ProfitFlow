package com.profitflow.core_app.product.service;

import com.profitflow.core_app.product.dto.ProductResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductResponse> listByMerchant(String merchantEmail);

    ProductResponse updateCostPrice(UUID productId, String merchantEmail, BigDecimal costPriceAmount, String currency);
}

