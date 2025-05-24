package cn.org.moying.domain.auth.service;


import cn.org.moying.api.response.Response;

/**
 * @Author: moying
 * @CreateTime: 2025-05-24
 * @Description:
 */

public interface IAuthService {

    Response<String> weixinQrCodeTicket();

    Response<String> weixinQrCodeTicket(String sceneStr);

    Response<String> checkLogin(String ticket);

    Response<String> checkLogin(String ticket, String sceneStr);

}
