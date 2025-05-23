package cn.org.moying.domain.order.adapter.port;


import cn.org.moying.domain.order.model.entity.MarketPayDiscountEntity;
import cn.org.moying.domain.order.model.entity.ProductEntity;

import java.util.Date;

public interface IProductPort {
    ProductEntity queryProductByProductId(String productId);

    MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId);

    void settlementMarketPayOrder(String userId, String orderId, Date payTime);
}
