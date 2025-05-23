package cn.org.moying.domain.order.service;

import cn.org.moying.domain.order.adapter.port.IProductPort;
import cn.org.moying.domain.order.adapter.repository.IOrderRepository;
import cn.org.moying.domain.order.model.aggregate.CreateOrderAggregate;
import cn.org.moying.domain.order.model.entity.MarketPayDiscountEntity;
import cn.org.moying.domain.order.model.entity.OrderEntity;
import cn.org.moying.domain.order.model.entity.PayOrderEntity;
import cn.org.moying.domain.order.model.valobj.MarketTypeVO;
import cn.org.moying.domain.order.model.valobj.OrderStatusVO;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderService extends AbstractOrderService{

    @Value("${alipay.notify_url}")
    private String notifyUrl;
    @Value("${alipay.return_url}")
    private String returnUrl;

    @Resource
    private AlipayClient alipayClient;

    public OrderService(IOrderRepository repository, IProductPort port) {
        super(repository, port);
    }

    @Override
    protected MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) {
        return port.lockMarketPayOrder(userId, teamId, activityId, productId, orderId);
    }



    @Override
    protected void doSaveOrder(CreateOrderAggregate orderAggregate) {
        repository.doSaveOrder(orderAggregate);
    }


    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount, MarketPayDiscountEntity marketPayDiscountEntity) throws AlipayApiException {
        BigDecimal payAmount = marketPayDiscountEntity==null?totalAmount : marketPayDiscountEntity.getPayPrice();


        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", payAmount.toString());
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderId(orderId);
        payOrderEntity.setPayUrl(form);
        payOrderEntity.setOrderStatus(OrderStatusVO.PAY_WAIT);


        // 营销信息
        payOrderEntity.setMarketType(null == marketPayDiscountEntity ? MarketTypeVO.NO_MARKET.getCode() : MarketTypeVO.GROUP_BUY_MARKET.getCode());
        payOrderEntity.setMarketDeductionAmount(null == marketPayDiscountEntity ? BigDecimal.ZERO : marketPayDiscountEntity.getDeductionPrice());
        payOrderEntity.setPayAmount(payAmount);

        repository.updateOrderPayInfo(payOrderEntity);

        return payOrderEntity;

    }
    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException {
       return doPrepayOrder(userId, productId, productName, orderId, totalAmount, null);
    }

    @Override
    public void changeOrderPaySuccess(String orderId, Date payTime) {

        OrderEntity orderEntity = repository.queryOrderByOrderId(orderId);

        if (null == orderEntity) {
            return ;
        }
        if(MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(orderEntity.getMarketType())){
            repository.changeMarketOrderPaySuccess(orderId);
            // 发起营销结算回调（http/rpc
            port.settlementMarketPayOrder(orderEntity.getUserId(),orderId,payTime);
            // todo  重试/补偿机制
            // 发起结算的http/rpc调用可能会失败，这个时候还会有增加job任务补偿。条件为，检查一笔走了拼团的订单，超过n分钟后，仍然没有做拼团结算状态变更。
            // 我们这里失败了，会抛异常，借助支付宝回调/job来重试。可以单独实现一个独立的job来处理。
        }else{
            repository.changeOrderPaySuccess(orderId,payTime);
        }
    }

    @Override
    public void changeOrderMarketSettlement(List<String> outTradeNoList) {
        repository.changeOrderMarketSettlement(outTradeNoList);
    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return repository.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return repository.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return repository.changeOrderClose(orderId);
    }



}
