package cn.org.moying.trigger.listener;

import cn.org.moying.domain.goods.service.IGoodsService;
import cn.org.moying.domain.order.adapter.event.PaySuccessMessageEvent;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@RocketMQMessageListener(topic = "pay_success_topic",
        consumerGroup = "pay_mall_success_consumer")
@Component
@Slf4j
public class OrderPaySuccessListener implements RocketMQListener<String> {


    @Resource
    private IGoodsService goodsService;

    @Override
    public void onMessage(String paySuccessMessageJson) {
        try {
            log.info("✅ 收到支付成功消息，开始处理业务: {}", paySuccessMessageJson);

            PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage = JSON.parseObject(paySuccessMessageJson, PaySuccessMessageEvent.PaySuccessMessage.class);

            log.info("模拟发货（如；发货、充值、开户员、返利），单号:{}", paySuccessMessage.getTradeNo());

            // 变更订单状态 - 发货完成&结算
            goodsService.changeOrderDealDone(paySuccessMessage.getTradeNo());

            // 可以打开测试，MQ 消费失败，会抛异常，之后重试消费。这个也是最终执行的重要手段。
            // throw new RuntimeException("重试消费");
        } catch (Exception e) {
            log.error("收到支付成功消息失败 {}", paySuccessMessageJson,e);
            throw e;
        }
    }
}