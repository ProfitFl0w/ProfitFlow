package com.profitflow.core_app.datalayer.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record NormalizedOrderItem(
        String externalOrderId,
        String externalProductId,
        LocalDateTime orderDate,
        NormalizedOrderStatus status,
        BigDecimal sellingPrice,
        Integer quantity,
        List<NormalizedFee> fees,
        String rawData
) {}