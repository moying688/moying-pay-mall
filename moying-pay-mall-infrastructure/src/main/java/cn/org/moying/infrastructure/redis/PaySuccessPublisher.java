package cn.org.moying.infrastructure.redis;

import cn.org.moying.domain.order.adapter.event.PaySuccessMessageEvent;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PaySuccessPublisher {

    private static final String TOPIC = "pay_success";

    @Resource
    private RedissonClient redissonClient;

    public void publish(PaySuccessMessageEvent.PaySuccessMessage message) {

        redissonClient.getTopic(TOPIC).publish(message);
    }
}
