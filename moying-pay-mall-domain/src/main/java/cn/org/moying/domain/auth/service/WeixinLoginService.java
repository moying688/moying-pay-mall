package cn.org.moying.domain.auth.service;

import cn.org.moying.domain.auth.adapter.port.ILoginPort;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Service
public class WeixinLoginService implements ILoginService {

    @Resource
    private ILoginPort loginPort;
    @Resource
    private Cache<String, String> openidToken;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public String createQrCodeTicket() throws Exception {
        return loginPort.createQrCodeTicket();
    }

    @Override
    public String createQrCodeTicket(String sceneStr) throws Exception {
        String ticket = loginPort.createQrCodeTicket();
        // 保存浏览器指纹信息和ticket映射关系
//        openidToken.put(sceneStr,ticket);
        redissonClient.getBucket(sceneStr).set(ticket);
        return ticket;
    }

    @Override
    public String checkLogin(String ticket) {
//        return openidToken.getIfPresent(ticket);
        return (String) redissonClient.getBucket(ticket).get();
    }

    @Override
    public String checkIsLogin(String sceneStr) {
        String ticket = (String) redissonClient.getBucket(sceneStr).get();
         return (String) redissonClient.getBucket(ticket).get(); // openId
    }

    @Override
    public String checkLogin(String ticket, String sceneStr) {
//        String cacheTicket = openidToken.getIfPresent(sceneStr);
        String cacheTicket = (String) redissonClient.getBucket(sceneStr).get();
        if (StringUtils.isBlank(cacheTicket) || !cacheTicket.equals(ticket)) return null;
        return checkLogin(ticket);
    }

    @Override
    public void saveLoginState(String ticket, String openid) throws IOException {
        // todo 关键点！此处将ticket以及用户的openid进行绑定
        // 保存登录信息
//        openidToken.put(ticket, openid);
        redissonClient.getBucket(ticket).set(openid);
        // 发送模板消息
        loginPort.sendLoginTemplate(openid);
    }



}
