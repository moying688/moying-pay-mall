package cn.org.moying.domain.goods.service;

import cn.org.moying.domain.goods.repository.IGoodsRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 结算服务
 */
@Service
public class GoodsService implements IGoodsService {

    @Resource
    private IGoodsRepository repository;


    @Override
    public void changeOrderDealDone(String orderId) {
        repository.changeOrderDealDone(orderId);
    }

}
