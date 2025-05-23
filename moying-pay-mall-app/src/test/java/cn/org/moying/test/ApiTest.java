package cn.org.moying.test;

import cn.org.moying.domain.order.adapter.event.PaySuccessMessageEvent;
import cn.org.moying.types.event.BaseEvent;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private EventBus eventBus;

    @Resource
    private PaySuccessMessageEvent paySuccessMessageEvent;

    @Test
    public void test() throws InterruptedException {

        BaseEvent.EventMessage<PaySuccessMessageEvent.PaySuccessMessage> paySuccessMessageEventMessage = paySuccessMessageEvent.buildEventMessage(
                PaySuccessMessageEvent.PaySuccessMessage.builder()
                        .tradeNo("1100000111")
                        .build());

        eventBus.post(JSON.toJSONString(paySuccessMessageEventMessage.getData()));
        log.info("测试完成");
        new CountDownLatch(1).await();
    }

}
