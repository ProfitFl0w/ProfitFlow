package com.profitflow.core_app.order_item.mapstruct;

import com.profitflow.core_app.common.Money;
import com.profitflow.core_app.datalayer.model.FeeType;
import com.profitflow.core_app.datalayer.model.NormalizedFee;
import com.profitflow.core_app.datalayer.model.NormalizedOrderItem;
import com.profitflow.core_app.datalayer.model.NormalizedOrderStatus;
import com.profitflow.core_app.order.entity.*;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps marketplace adapter data (OrderItem entities) to the normalized DataLayer model.
 * Used by marketplace adapters (Kaspi, Ozon, WB) to feed data into ProfitCalculationService.
 *
 * Note: fee amounts are stored as NEGATIVE values in NormalizedFee
 * so that ProfitCalculationService formula (grossRevenue + totalFees - costPrice) produces correct results.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    default NormalizedOrderItem toNormalized(OrderItem item, List<OrderItemAdjustment> adjustments) {
        Order order = item.getOrder();

        List<NormalizedFee> fees = buildFees(item, adjustments);
        NormalizedOrderStatus status = mapStatus(order.getStatus());

        return new NormalizedOrderItem(
                order.getExternalId(),
                item.getProduct().getSku(),
                order.getOrderDate().toLocalDateTime(),
                status,
                item.getSellingPrice().toDecimal(),
                item.getQuantity(),
                fees,
                order.getRawData()
        );
    }

    default List<NormalizedFee> buildFees(OrderItem item, List<OrderItemAdjustment> adjustments) {
        List<NormalizedFee> fees = new ArrayList<>();

        addFeeIfNonZero(fees, FeeType.PLATFORM_COMMISSION, item.getPlatformFee());
        addFeeIfNonZero(fees, FeeType.LOGISTICS, item.getLogisticsFee());
        addFeeIfNonZero(fees, FeeType.OTHER, item.getOtherFees());

        if (adjustments != null) {
            for (OrderItemAdjustment adjustment : adjustments) {
                FeeType feeType = mapAdjustmentType(adjustment.getType());
                BigDecimal amount = adjustment.getAmount().toDecimal();
                fees.add(new NormalizedFee(feeType, amount, adjustment.getReason()));
            }
        }

        return fees;
    }

    private void addFeeIfNonZero(List<NormalizedFee> fees, FeeType type, Money money) {
        if (money != null && money.getAmount() != null && money.getAmount() != 0) {
            fees.add(new NormalizedFee(type, money.toDecimal().negate(), null));
        }
    }

    default NormalizedOrderStatus mapStatus(OrderStatus status) {
        return switch (status) {
            case RETURNED, CANCELLED -> NormalizedOrderStatus.RETURNED;
            default -> NormalizedOrderStatus.ACTIVE;
        };
    }

    default FeeType mapAdjustmentType(OrderItemAdjustmentType type) {
        return switch (type) {
            case COMMISSION_CHANGE -> FeeType.PLATFORM_COMMISSION;
            case PENALTY -> FeeType.PENALTY;
            case STORAGE_FEE -> FeeType.STORAGE;
            case LOGISTICS_ADJ -> FeeType.LOGISTICS;
            case REFUND_ADJ -> FeeType.OTHER;
        };
    }
}