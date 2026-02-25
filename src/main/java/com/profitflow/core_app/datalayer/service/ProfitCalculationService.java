package com.profitflow.core_app.datalayer.service;

import com.profitflow.core_app.datalayer.model.NormalizedOrderItem;
import com.profitflow.core_app.datalayer.model.ProfitResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProfitCalculationService {
    ProfitResult calculate(NormalizedOrderItem item, BigDecimal costPricePerUnit);

    ProfitResult aggregateAll(List<NormalizedOrderItem> items,
                              Map<String, BigDecimal> costPricesByExternalProductId);
}