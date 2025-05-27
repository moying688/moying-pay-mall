package cn.org.moying.trigger.listener;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author: moying
 * @CreateTime: 2025-05-27
 * @Description:
 */


@RocketMQMessageListener(topic = "group_team_success_topic",
        consumerGroup = "pay_mall_success_consumer")
@Component
public class TeamSuccessTopicListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        System.out.println("接收到消息：" + message);
    }

}
