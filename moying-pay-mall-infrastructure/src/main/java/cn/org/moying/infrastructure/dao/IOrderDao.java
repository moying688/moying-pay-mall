package cn.org.moying.infrastructure.dao;

import cn.org.moying.domain.order.model.entity.OrderEntity;
import cn.org.moying.infrastructure.dao.po.PayOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IOrderDao {

    void insert(PayOrder payOrder);

    PayOrder queryUnPayOrder(PayOrder payOrder);

    void updateOrderPayInfo(PayOrder payOrder);

    void changeOrderPaySuccess(PayOrder payOrderReq);

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    PayOrder queryOrderByOrderId(String orderId);


    void changeOrderMarketSettlement(@Param("outTradeNoList")List<String> outTradeNoList);

    void changeOrderDealDone(String orderId);
}
