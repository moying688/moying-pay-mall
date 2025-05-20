package cn.org.moying.infrastructure.gateway.dto;

import lombok.Data;

/**
 * @description 获取微信登录二维码响应对象
 */
@Data
public class WeixinQrCodeResponseDTO {

    private String ticket;
    private Long expire_seconds;
    private String url;

}
