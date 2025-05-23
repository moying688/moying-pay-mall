package cn.org.moying.domain.goods.repository;

/**
 * @description 结算仓储
 */
public interface IGoodsRepository {

    void changeOrderDealDone(String orderId);

}
