package cn.org.moying.domain.auth.adapter.port;

import java.io.IOException;

/**
 * @Author: moying
 * @CreateTime: 2025-05-20
 * @Description:
 */

public interface ILoginPort {

    String createQrCodeTicket() throws IOException;

    String createQrCodeTicket(String sceneStr) throws IOException;

    void sendLoginTemplate(String openid) throws IOException;



}