package com.profitflow.core_app.datalayer.service.impl;

import com.profitflow.core_app.datalayer.model.FeeType;
import com.profitflow.core_app.datalayer.model.NormalizedFee;
import com.profitflow.core_app.datalayer.model.NormalizedOrderItem;
import com.profitflow.core_app.datalayer.model.ProfitResult;
import com.profitflow.core_app.datalayer.service.ProfitCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ProfitCalculationServiceImpl implements ProfitCalculationService {

    @Override
    public ProfitResult calculate(NormalizedOrderItem item, BigDecimal costPricePerUnit) {
        BigDecimal safeCostPerUnit = nonNullOrZero(costPricePerUnit);

        BigDecimal grossRevenue = nonNullOrZero(item.sellingPrice())
                .multiply(BigDecimal.valueOf(item.quantity()));

        BigDecimal totalFees = item.fees().stream()
                .map(NormalizedFee::amount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = safeCostPerUnit.multiply(BigDecimal.valueOf(item.quantity()));

        BigDecimal netProfit = grossRevenue
                .add(totalFees)
                .subtract(totalCost);

        BigDecimal marginPercent = calculateMarginPercent(grossRevenue, netProfit);

        Map<FeeType, BigDecimal> feeBreakdown = aggregateFeesByType(item.fees());

        return new ProfitResult(
                grossRevenue,
                totalFees,
                totalCost,
                netProfit,
                marginPercent,
                feeBreakdown
        );
    }

    @Override
    public ProfitResult aggregateAll(List<NormalizedOrderItem> items,
                                     Map<String, BigDecimal> costPricesByExternalProductId) {
        List<ProfitResult> results = items.stream()
                .map(item -> {
                    BigDecimal costPricePerUnit = costPricesByExternalProductId.get(item.externalProductId());
                    return calculate(item, costPricePerUnit);
                })
                .toList();

        return mergeResults(results);
    }

    private ProfitResult mergeResults(List<ProfitResult> results) {
        BigDecimal grossRevenue = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<FeeType, BigDecimal> feeBreakdown = new EnumMap<>(FeeType.class);

        for (ProfitResult result : results) {
            grossRevenue = grossRevenue.add(nonNullOrZero(result.grossRevenue()));
            totalFees = totalFees.add(nonNullOrZero(result.totalFees()));
            totalCost = totalCost.add(nonNullOrZero(result.costPrice()));

            for (Map.Entry<FeeType, BigDecimal> entry : result.feeBreakdown().entrySet()) {
                BigDecimal current = feeBreakdown.getOrDefault(entry.getKey(), BigDecimal.ZERO);
                feeBreakdown.put(entry.getKey(), current.add(nonNullOrZero(entry.getValue())));
            }
        }

        BigDecimal netProfit = grossRevenue.add(totalFees).subtract(totalCost);
        BigDecimal marginPercent = calculateMarginPercent(grossRevenue, netProfit);

        return new ProfitResult(
                grossRevenue,
                totalFees,
                totalCost,
                netProfit,
                marginPercent,
                feeBreakdown
        );
    }

    private Map<FeeType, BigDecimal> aggregateFeesByType(List<NormalizedFee> fees) {
        Map<FeeType, BigDecimal> result = new EnumMap<>(FeeType.class);
        for (NormalizedFee fee : fees) {
            BigDecimal amount = nonNullOrZero(fee.amount());
            FeeType type = fee.type();
            BigDecimal current = result.getOrDefault(type, BigDecimal.ZERO);
            result.put(type, current.add(amount));
        }
        return result;
    }

    private BigDecimal calculateMarginPercent(BigDecimal grossRevenue, BigDecimal netProfit) {
        if (grossRevenue == null || grossRevenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return netProfit
                .divide(grossRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private BigDecimal nonNullOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}

