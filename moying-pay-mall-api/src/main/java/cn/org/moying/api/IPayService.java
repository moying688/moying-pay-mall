package cn.org.moying.api;

import cn.org.moying.api.dto.CreatePayRequestDTO;
import cn.org.moying.api.dto.NotifyRequestDTO;
import cn.org.moying.api.response.Response;

public interface IPayService {

    Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);

    String groupBuyNotify(NotifyRequestDTO requestDTO);
}
