package cn.org.moying.trigger.listener;

import cn.org.moying.api.dto.NotifyRequestDTO;
import cn.org.moying.domain.order.service.IOrderService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: moying
 * @CreateTime: 2025-05-27
 * @Description:
 */


@RocketMQMessageListener(topic = "group_team_success_topic",
        consumerGroup = "group_buy_team_success_consumer",
        messageModel = MessageModel.BROADCASTING)
@Component
@Slf4j
public class TeamSuccessTopicListener implements RocketMQListener<String> {

    @Resource
    private IOrderService orderService;
    @Override
    public void onMessage(String message) {
        try {
            NotifyRequestDTO requestDTO = JSON.parseObject(message, NotifyRequestDTO.class);
            log.info("拼团回调，组队完成，结算开始 {}", JSON.toJSONString(requestDTO));
            // 营销结算
            orderService.changeOrderMarketSettlement(requestDTO.getOutTradeNoList());
        } catch (Exception e) {
            log.error("拼团回调，组队完成，结算失败 {}", message, e);
            throw e;
        }
    }

}
