package cn.org.moying.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description 营销支付锁单应答对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockMarketPayOrderResponseDTO {

    /** 预购订单ID */
    private String orderId;
    /** 原始价格 */
    private BigDecimal originalPrice;
    /** 折扣金额 */
    private BigDecimal deductionPrice;
    /** 支付金额 */
    private BigDecimal payPrice;
    /** 交易订单状态 */
    private Integer tradeOrderStatus;


}
