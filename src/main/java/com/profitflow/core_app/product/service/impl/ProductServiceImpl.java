package com.profitflow.core_app.product.service.impl;

import com.profitflow.core_app.common.Money;
import com.profitflow.core_app.exception.AppException;
import com.profitflow.core_app.exception.ErrorCode;
import com.profitflow.core_app.product.dto.ProductResponse;
import com.profitflow.core_app.product.entity.Product;
import com.profitflow.core_app.product.repository.ProductRepository;
import com.profitflow.core_app.product.service.ProductService;
import com.profitflow.core_app.security.entity.Merchant;
import com.profitflow.core_app.security.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public List<ProductResponse> listByMerchant(String merchantEmail) {
        Merchant merchant = findMerchantByEmail(merchantEmail);
        List<Product> products = productRepository.findAllActiveByMerchantId(merchant.getId());
        return products.stream()
                .map(this::toProductResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse updateCostPrice(UUID productId, String merchantEmail, BigDecimal costPriceAmount, String currency) {
        Merchant merchant = findMerchantByEmail(merchantEmail);
        Product product = productRepository.findById(productId)
                .filter(p -> !p.isDeleted())
                .filter(p -> p.getMerchant().getId().equals(merchant.getId()))
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Money newCostPrice = Money.of(costPriceAmount, currency);

        product.setCostPrice(newCostPrice);

        Product saved = productRepository.save(product);

        return toProductResponse(saved);
    }

    private Merchant findMerchantByEmail(String email) {
        return merchantRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.MERCHANT_NOT_FOUND));
    }

    private ProductResponse toProductResponse(Product product) {
        BigDecimal costAmount = product.getCostPrice() != null ? product.getCostPrice().toDecimal() : null;
        String costCurrency = product.getCostPrice() != null ? product.getCostPrice().getCurrency() : null;

        return new ProductResponse (
                product.getId(),
                product.getSku(),
                product.getName(),
                costAmount,
                null,
                costCurrency
        );
    }
}