package com.profitflow.core_app.service;

import com.profitflow.core_app.entity.order.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<Product> listByMerchant(String merchantEmail);

    Product updateCostPrice(UUID productId, String merchantEmail, BigDecimal costPriceAmount, String currency);
}
