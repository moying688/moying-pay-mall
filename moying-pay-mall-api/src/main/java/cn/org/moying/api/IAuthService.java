package cn.org.moying.api;

import cn.org.moying.api.response.Response;

public interface IAuthService {

    Response<String> weixinQrCodeTicket();

    Response<String> weixinQrCodeTicket( String sceneStr);

    Response<String> checkLogin(String ticket);

    Response<String> checkLogin( String ticket, String sceneStr);
}
