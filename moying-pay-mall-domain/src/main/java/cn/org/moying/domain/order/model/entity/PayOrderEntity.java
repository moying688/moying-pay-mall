package cn.org.moying.domain.order.model.entity;

import cn.org.moying.domain.order.model.valobj.OrderStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderEntity {

    private String userId;
    private String orderId;
    private String payUrl;
    private OrderStatusVO orderStatus;

    // 营销类型；0无营销、1拼团营销
    private Integer marketType;
    // 营销金额；优惠金额
    private BigDecimal marketDeductionAmount;
    // 支付金额
    private BigDecimal payAmount;

}
