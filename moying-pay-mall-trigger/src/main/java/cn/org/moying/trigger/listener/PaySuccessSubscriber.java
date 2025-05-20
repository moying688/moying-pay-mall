package cn.org.moying.trigger.listener;

import cn.org.moying.domain.order.adapter.event.PaySuccessMessageEvent;
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

    @PostConstruct
    public void subscribe() {
        redissonClient.getTopic(TOPIC).addListener(PaySuccessMessageEvent.PaySuccessMessage.class, (channel, msg) -> {
            log.info("✅ 收到支付成功消息，开始处理业务: {}", msg);
            // todo 发货 / 返利 等逻辑
        });
    }
}
