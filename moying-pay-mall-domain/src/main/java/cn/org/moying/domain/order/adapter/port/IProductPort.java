package cn.org.moying.domain.order.adapter.port;


import cn.org.moying.domain.order.model.entity.ProductEntity;

public interface IProductPort {
    ProductEntity queryProductByProductId(String productId);

}
