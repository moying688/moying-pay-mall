package cn.org.moying.domain.auth.service;

import java.io.IOException;

/**
 * @Author: moying
 * @CreateTime: 2025-05-20
 * @Description:
 */

public interface ILoginService {

    String createQrCodeTicket() throws Exception;

    String checkLogin(String ticket);

    void saveLoginState(String ticket, String openid) throws IOException;

}