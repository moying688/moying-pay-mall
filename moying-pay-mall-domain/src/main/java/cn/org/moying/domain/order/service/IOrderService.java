package cn.org.moying.domain.order.service;


import cn.org.moying.domain.order.model.entity.PayOrderEntity;
import cn.org.moying.domain.order.model.entity.ShopCartEntity;

import java.util.Date;
import java.util.List;

public interface IOrderService {

    PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception;
    void changeOrderPaySuccess(String orderId, Date payTime);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    void changeOrderMarketSettlement(List<String> outTradeNoList);
}
