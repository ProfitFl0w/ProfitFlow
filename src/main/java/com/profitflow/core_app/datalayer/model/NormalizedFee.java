package com.profitflow.core_app.datalayer.model;

import java.math.BigDecimal;

public record NormalizedFee(
        FeeType type,
        BigDecimal amount,
        String description
) {}