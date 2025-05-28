package cn.org.moying.domain.auth.service;

import java.io.IOException;

/**
 * @Author: moying
 * @CreateTime: 2025-05-20
 * @Description:
 */

public interface ILoginService {

    String createQrCodeTicket() throws Exception;

    String createQrCodeTicket(String sceneStr) throws Exception;

    String checkLogin(String ticket);

    String checkLogin(String ticket, String sceneStr);

    void saveLoginState(String ticket, String openid) throws IOException;

}