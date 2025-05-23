package cn.org.moying.infrastructure.adapter.repository;

import cn.org.moying.domain.goods.repository.IGoodsRepository;
import cn.org.moying.infrastructure.dao.IOrderDao;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @description 结算仓储服务
 */
@Repository
public class GoodsRepository implements IGoodsRepository {

    @Resource
    private IOrderDao orderDao;

    @Override
    public void changeOrderDealDone(String orderId) {
        orderDao.changeOrderDealDone(orderId);
    }

}
