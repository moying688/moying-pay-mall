package cn.org.moying.domain.order.service;
import cn.org.moying.domain.order.adapter.port.IProductPort;
import cn.org.moying.domain.order.adapter.repository.IOrderRepository;
import cn.org.moying.domain.order.model.aggregate.CreateOrderAggregate;
import cn.org.moying.domain.order.model.entity.*;
import cn.org.moying.domain.order.model.valobj.MarketTypeVO;
import cn.org.moying.domain.order.model.valobj.OrderStatusVO;
import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public abstract class AbstractOrderService implements IOrderService {

    protected final IOrderRepository repository;
    protected final IProductPort port;

    public AbstractOrderService(IOrderRepository repository, IProductPort port) {
        this.repository = repository;
        this.port = port;
    }

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception {
        // 1. 查询当前用户是否存在掉单和未支付订单
        OrderEntity unpaidOrderEntity = repository.queryUnPayOrder(shopCartEntity);

        if (null != unpaidOrderEntity && OrderStatusVO.PAY_WAIT.equals(unpaidOrderEntity.getOrderStatusVO())) {
            log.info("创建订单-存在，已存在未支付订单。userId:{} productId:{} orderId:{}", shopCartEntity.getUserId(),
                    shopCartEntity.getProductId(), unpaidOrderEntity.getOrderId());
            return PayOrderEntity.builder()
                    .orderId(unpaidOrderEntity.getOrderId())
                    .payUrl(unpaidOrderEntity.getPayUrl())
                    .build();
        } else if (null != unpaidOrderEntity && OrderStatusVO.CREATE.equals(unpaidOrderEntity.getOrderStatusVO())) {
            // 外部接口网络超时( 支付宝创建支付单失败 或者拼团营销服务失败)
            log.info("创建订单-存在，存在未创建支付单订单，创建支付单开始 userId:{} productId:{} orderId:{}", shopCartEntity.getUserId(),
                    shopCartEntity.getProductId(), unpaidOrderEntity.getOrderId());
            Integer marketType = unpaidOrderEntity.getMarketType();
            BigDecimal marketDeductionAmount = unpaidOrderEntity.getMarketDeductionAmount();

            PayOrderEntity payOrderEntity = null;

            if(MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(marketType) && null == marketDeductionAmount) {
                // 有拼团营销 且 未计算营销金额
                MarketPayDiscountEntity marketPayDiscountEntity = this.lockMarketPayOrder(shopCartEntity.getUserId(),
                        shopCartEntity.getTeamId(),
                        shopCartEntity.getActivityId(),
                        shopCartEntity.getProductId(),
                        unpaidOrderEntity.getOrderId());
                payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(),
                        unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getTotalAmount(), marketPayDiscountEntity);
            } else {
                // 无拼团营销
                payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(),
                        unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getTotalAmount());
            }

            return PayOrderEntity.builder()
                    .orderId(payOrderEntity.getOrderId())
                    .payUrl(payOrderEntity.getPayUrl())
                    .build();
        }

        // 2. 查询商品信息
        ProductEntity productEntity = port.queryProductByProductId(shopCartEntity.getProductId());

        // 3. 订单实体信息
        OrderEntity orderEntity = CreateOrderAggregate.buildOrderEntity(productEntity.getProductId(), productEntity.getProductName());

        // 4. 订单聚合信息
        CreateOrderAggregate orderAggregate = CreateOrderAggregate.builder()
                .userId(shopCartEntity.getUserId())
                .productEntity(productEntity)
                .orderEntity(orderEntity)
                .build();

        // 创建本地订单
        this.doSaveOrder(orderAggregate);

        // 5. 营销锁单
        MarketPayDiscountEntity marketPayDiscountEntity = null;
        if(MarketTypeVO.GROUP_BUY_MARKET.equals(shopCartEntity.getMarketTypeVO())){
            marketPayDiscountEntity = this.lockMarketPayOrder(shopCartEntity.getUserId(),
                    shopCartEntity.getTeamId(),
                    shopCartEntity.getActivityId(),
                    shopCartEntity.getProductId(),
                    orderEntity.getOrderId());
        }

        // 支付宝生成支付单
        PayOrderEntity payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(),
                productEntity.getProductId(),
                productEntity.getProductName(),
                orderEntity.getOrderId(),
                productEntity.getPrice(),
                marketPayDiscountEntity);
        log.info("创建订单-完成，生成支付单。userId: {} orderId: {} payUrl: {}", shopCartEntity.getUserId(), orderEntity.getOrderId(), payOrderEntity.getPayUrl());

        return PayOrderEntity.builder()
                .orderId(orderEntity.getOrderId())
                .payUrl(payOrderEntity.getPayUrl())
                .build();
    }

    protected abstract MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId);

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount, MarketPayDiscountEntity marketPayDiscountEntity) throws AlipayApiException;

    protected abstract void doSaveOrder(CreateOrderAggregate orderAggregate);
    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException;


}
