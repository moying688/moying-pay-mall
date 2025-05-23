package cn.org.moying.infrastructure.adapter.repository;

import cn.org.moying.domain.order.adapter.event.PaySuccessMessageEvent;
import cn.org.moying.domain.order.adapter.repository.IOrderRepository;
import cn.org.moying.domain.order.model.aggregate.CreateOrderAggregate;
import cn.org.moying.domain.order.model.entity.OrderEntity;
import cn.org.moying.domain.order.model.entity.PayOrderEntity;
import cn.org.moying.domain.order.model.entity.ProductEntity;
import cn.org.moying.domain.order.model.entity.ShopCartEntity;
import cn.org.moying.domain.order.model.valobj.OrderStatusVO;
import cn.org.moying.infrastructure.dao.IOrderDao;
import cn.org.moying.infrastructure.dao.po.PayOrder;
import cn.org.moying.infrastructure.redis.PaySuccessPublisher;
import cn.org.moying.types.event.BaseEvent;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.EventBus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Repository
public class OrderRepository implements IOrderRepository {

    @Resource
    private IOrderDao orderDao;

    @Resource
    private PaySuccessMessageEvent paySuccessMessageEvent;
    @Resource
    private EventBus eventBus;

    @Resource
    private PaySuccessPublisher paySuccessPublisher;

    @Override
    public void doSaveOrder(CreateOrderAggregate orderAggregate) {
        String userId = orderAggregate.getUserId();
        ProductEntity productEntity = orderAggregate.getProductEntity();
        OrderEntity orderEntity = orderAggregate.getOrderEntity();

        PayOrder order = new PayOrder();
        order.setUserId(userId);
        order.setProductId(productEntity.getProductId());
        order.setProductName(productEntity.getProductName());
        order.setOrderId(orderEntity.getOrderId());
        order.setOrderTime(orderEntity.getOrderTime());
        order.setTotalAmount(productEntity.getPrice());
        order.setStatus(orderEntity.getOrderStatusVO().getCode());
        order.setMarketType(orderEntity.getMarketType());
        order.setPayAmount(productEntity.getPrice());

        orderDao.insert(order);
    }

    @Override
    public OrderEntity queryUnPayOrder(ShopCartEntity shopCartEntity) {
        // 1. 封装参数
        PayOrder orderReq = new PayOrder();
        orderReq.setUserId(shopCartEntity.getUserId());
        orderReq.setProductId(shopCartEntity.getProductId());

        // 2. 查询到订单
        PayOrder order = orderDao.queryUnPayOrder(orderReq);
        if (null == order) return null;

        // 3. 返回结果
        return OrderEntity.builder()
                .productId(order.getProductId())
                .productName(order.getProductName())
                .orderId(order.getOrderId())
                .orderStatusVO(OrderStatusVO.valueOf(order.getStatus()))
                .orderTime(order.getOrderTime())
                .totalAmount(order.getTotalAmount())
                .payUrl(order.getPayUrl())
                .marketType(order.getMarketType())
                .marketDeductionAmount(order.getMarketDeductionAmount())
                .payAmount(order.getPayAmount())
                .build();
    }

    @Override
    public void updateOrderPayInfo(PayOrderEntity payOrderEntity) {
        PayOrder payOrderReq = PayOrder.builder()
                .userId(payOrderEntity.getUserId())
                .orderId(payOrderEntity.getOrderId())
                .status(payOrderEntity.getOrderStatus().getCode())
                .payUrl(payOrderEntity.getPayUrl())
                .payAmount(payOrderEntity.getPayAmount())
                .marketDeductionAmount(payOrderEntity.getMarketDeductionAmount())
                .marketType(payOrderEntity.getMarketType())
                .build();
        orderDao.updateOrderPayInfo(payOrderReq);
    }

    @Override
    public void changeOrderPaySuccess(String orderId, Date payTime) {
        PayOrder payOrderReq = new PayOrder();
        payOrderReq.setOrderId(orderId);
        payOrderReq.setPayTime(payTime);
        payOrderReq.setStatus(OrderStatusVO.PAY_SUCCESS.getCode());
        orderDao.changeOrderPaySuccess(payOrderReq);

        BaseEvent.EventMessage<PaySuccessMessageEvent.PaySuccessMessage> paySuccessMessageEventMessage =
                paySuccessMessageEvent.buildEventMessage(PaySuccessMessageEvent.PaySuccessMessage.builder().tradeNo(orderId).build());
        PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage = paySuccessMessageEventMessage.getData();

//        eventBus.post(paySuccessMessage);
        paySuccessPublisher.publish(JSON.toJSONString(paySuccessMessage));
    }

    @Override
    public void changeMarketOrderPaySuccess(String orderId) {
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderId(orderId);
        payOrder.setStatus(OrderStatusVO.PAY_SUCCESS.getCode());
        orderDao.changeOrderPaySuccess(payOrder);
    }

    @Override
    public void changeOrderMarketSettlement(List<String> outTradeNoList) {
        // 更新拼团状态
        orderDao.changeOrderMarketSettlement(outTradeNoList);

        // 循环成功发送消息 -
        // 一般场景里，还会有job任务扫描超时没有结算的订单，查询订单状态。查询对方服务端的接口，会被限制一次查询多少，频次多少。
        outTradeNoList.forEach(outTradeNo->{
            BaseEvent.EventMessage<PaySuccessMessageEvent.PaySuccessMessage> paySuccessMessageEventMessage = paySuccessMessageEvent.buildEventMessage(
                    PaySuccessMessageEvent.PaySuccessMessage.builder()
                            .tradeNo(outTradeNo)
                            .build());
            PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage  = paySuccessMessageEventMessage.getData();
            paySuccessPublisher.publish(JSON.toJSONString(paySuccessMessage));
//            eventBus.post(JSON.toJSONString(paySuccessMessage));
        });
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return orderDao.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return orderDao.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return orderDao.changeOrderClose(orderId);
    }

    @Override
    public OrderEntity queryOrderByOrderId(String orderId) {
        PayOrder payOrder = orderDao.queryOrderByOrderId(orderId);
        if (null == payOrder) return null;
       return OrderEntity.builder()
                .productId(payOrder.getProductId())
                .productName(payOrder.getProductName())
                .orderId(payOrder.getOrderId())
                .orderTime(payOrder.getOrderTime())
                .totalAmount(payOrder.getTotalAmount())
                .orderStatusVO(OrderStatusVO.valueOf(payOrder.getStatus()))
                .payUrl(payOrder.getPayUrl())
                .marketType(payOrder.getMarketType())
                .marketDeductionAmount(payOrder.getMarketDeductionAmount())
                .payAmount(payOrder.getPayAmount())
                .userId(payOrder.getUserId())
                .build();
    }




}
