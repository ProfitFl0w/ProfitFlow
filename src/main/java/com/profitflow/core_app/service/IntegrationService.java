package com.profitflow.core_app.service;

import com.profitflow.core_app.entity.integration.Integration;
import com.profitflow.core_app.entity.integration.Platform;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IntegrationService {

    List<Integration> listByMerchant(String merchantEmail);

    Integration create(String merchantEmail, Platform platform, String shopName, String apiKey,
                       String currency, BigDecimal exchangeRate);

    Integration getByIdAndMerchant(UUID integrationId, String merchantEmail);
}
