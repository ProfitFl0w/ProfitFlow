package com.profitflow.core_app.integration.service;

import com.profitflow.core_app.integration.entity.Integration;
import com.profitflow.core_app.integration.entity.Platform;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IntegrationService {
    List<Integration> listByMerchant(String merchantEmail);

    Integration create(String merchantEmail, Platform platform, String shopName, String apiKey,
                       String currency, BigDecimal exchangeRate);

    Integration getByIdAndMerchant(UUID integrationId, String merchantEmail);
}