package cn.org.moying.infrastructure.gateway;

import cn.org.moying.infrastructure.gateway.dto.LockMarketPayOrderRequestDTO;
import cn.org.moying.infrastructure.gateway.dto.LockMarketPayOrderResponseDTO;
import cn.org.moying.infrastructure.gateway.response.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @Author: moying
 * @CreateTime: 2025-05-23
 * @Description:
 */

public interface IGroupBuyMarketService {

    /**
     * 营销锁单（调用外部拼团服务接口
     * @param lockMarketPayOrderRequestDTO 锁单商品信息
     * @return 锁单结果信息
     */
    @POST("api/v1/gbm/trade/lock_market_pay_order")
    Call<Response<LockMarketPayOrderResponseDTO>> lockMarketPayOrder(@Body LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO);
}
