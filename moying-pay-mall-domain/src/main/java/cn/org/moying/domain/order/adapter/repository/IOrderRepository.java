package cn.org.moying.domain.order.adapter.repository;


import cn.org.moying.domain.order.model.aggregate.CreateOrderAggregate;
import cn.org.moying.domain.order.model.entity.OrderEntity;
import cn.org.moying.domain.order.model.entity.PayOrderEntity;
import cn.org.moying.domain.order.model.entity.ShopCartEntity;

import java.util.Date;
import java.util.List;

public interface IOrderRepository {
    void doSaveOrder(CreateOrderAggregate orderAggregate);

    OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity);

    void updateOrderPayInfo(PayOrderEntity payOrderEntity);

    void changeOrderPaySuccess(String orderId, Date payTime);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    OrderEntity queryOrderByOrderId(String orderId);

    void changeMarketOrderPaySuccess(String orderId);

    void changeOrderMarketSettlement(List<String> outTradeNoList);
}
