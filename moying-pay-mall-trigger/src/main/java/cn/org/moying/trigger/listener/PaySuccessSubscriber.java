package cn.org.moying.trigger.listener;

import cn.org.moying.domain.goods.service.IGoodsService;
import cn.org.moying.domain.order.adapter.event.PaySuccessMessageEvent;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Component
public class PaySuccessSubscriber {

    private static final String TOPIC = "pay_success";

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IGoodsService goodsService;

    @PostConstruct
    public void subscribe() {
        redissonClient.getTopic(TOPIC).addListener(String.class, (channel, msg) -> {
            log.info("✅ 收到支付成功消息，开始处理业务: {}", msg);
            // todo 发货 / 返利 等逻辑

            PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage = JSON.parseObject(msg, PaySuccessMessageEvent.PaySuccessMessage.class);

            log.info("模拟发货（如；发货、充值、开户员、返利），单号:{}", paySuccessMessage.getTradeNo());

            // 变更订单状态 - 发货完成&结算
            goodsService.changeOrderDealDone(paySuccessMessage.getTradeNo());
        });
    }
}
